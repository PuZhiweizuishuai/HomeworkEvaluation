package com.buguagaoshu.homework.oss.controller;

import com.buguagaoshu.homework.common.domain.FileModel;
import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.domain.VditorFiles;
import com.buguagaoshu.homework.common.utils.AesUtil;
import com.buguagaoshu.homework.oss.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.oss.repository.FileStorageRepository;
import com.buguagaoshu.homework.oss.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 17:47
 */
@RestController
@RefreshScope
@Slf4j
public class FileController {
    private final FileStorageRepository repository;

    @Value("${Aes.key}")
    public String AES_KEY;

    public FileController(FileStorageRepository fileStorageRepository) {
        this.repository = fileStorageRepository;
    }


    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @PostMapping("/uploads/file")
    public VditorFiles upload(@RequestParam(value = "file[]", required = false) MultipartFile[] files,
                              @RequestParam(value = "type", required = false) String type,
                              @RequestParam(value = "homework", required = false) Long homework,
                              HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(2);
        map.put("type", type);
        map.put("homework", String.valueOf(homework));
        return repository.save(files,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId(),
                map, request);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/uploads/courseware")
    public ResponseDetails upload(@RequestParam(value = "file", required = false) MultipartFile file,
                                  @RequestParam(value = "course") String course,
                                  HttpServletRequest request) {
        if (course == null) {
            return ResponseDetails.ok(0, "缺少课程ID号！");
        }
        Map<String, String> save = repository.save(file, course, request);
        if (save == null) {
            return ResponseDetails.ok(0, "上传出现异常，请重新尝试");
        }
        return ResponseDetails.ok().put("data", save);
    }



    @PostMapping("/uploads/delete")
    public ResponseDetails deleteFile(@RequestBody FileModel fileModel) {
        if (StringUtils.isEmpty(fileModel.getToken())) {
            return ResponseDetails.ok().put("data", false);
        }
        try {
            Claims claims = JwtUtil.parseJWT(TokenAuthenticationHelper.SECRET_KEY, fileModel.getToken());
            if (fileModel.getPath().contains(claims.getId())) {
                log.info("用户 {} 操作删除文件： {}", claims.getId(), fileModel.getPath());
                return ResponseDetails.ok().put("data", repository.delete(fileModel.getPath()));
            } else {
                return ResponseDetails.ok().put("data", false);
            }
        } catch (Exception e) {
            return ResponseDetails.ok().put("data", false);
        }
    }


    @GetMapping("/uploads/file/{userId}/{date}/{filename:.+}")
    public HttpEntity getFile(@PathVariable("userId") String userId,
                                      @PathVariable("date") String date,
                                      @PathVariable("filename") String filename,
                                      HttpServletRequest request) {
        String path = "uploads/file/" + userId + "/" + date + "/" + filename;
        String url = repository.getFileUrl(path);
        if (url.equals("")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("404 Not Found!");
        }
        return ResponseEntity.status(HttpStatus.FOUND).header("location", url).body(null);
    }


    @GetMapping("/uploads/courseware/{course}/{userId}/{date}/{filename:.+}")
    public HttpEntity getFile(@PathVariable("course") String course,
                              @PathVariable("userId") String userId,
                              @PathVariable("date") String date,
                              @PathVariable("filename") String filename,
                              @RequestParam(value = "key", required = false) String key,
                              HttpServletRequest request) {
        String path = "uploads/courseware/" + course + "/" + userId + "/" + date + "/" + filename;
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        boolean lock = false;
        if (StringUtils.isEmpty(key)) {
            return ResponseEntity.status(HttpStatus.OK).body("key error!!!");
        }
        try {
            String decrypt = AesUtil.decrypt(key, AES_KEY);
            String[] str = decrypt.split("#");
            // 第一个是用户ID，第二个是文件名，第三个是过期时间
            if ((str[1].equals(filename) || (str[1]+".pdf").equals(filename)) && Long.parseLong(str[2]) > System.currentTimeMillis() && str[0].equals(user.getId())) {
                lock = true;
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body("key error!!!");
        }
        if (lock) {
            try {
                return ResponseEntity.status(HttpStatus.FOUND).header("location", repository.getFileUrl(path)).body(null);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("key error!!!");
    }
}
