package com.buguagaoshu.homework.evaluation.file;


import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.model.VditorFiles;
import com.buguagaoshu.homework.evaluation.repository.FileStorageRepository;
import com.buguagaoshu.homework.evaluation.utils.FileUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-08 13:11
 * TODO 文件格式验证,此加载器有BUG，先单独搞出来
 */
@RestController
public class FileController {
    private final FileStorageRepository repository;

    private final ResourceLoader resourceLoader;

    private final FileUtil fileUtil;

    @Autowired
    public FileController(FileStorageRepository repository, ResourceLoader resourceLoader, FileUtil fileUtil) {
        this.repository = repository;
        this.resourceLoader = resourceLoader;
        this.fileUtil = fileUtil;
    }

    @GetMapping("/upload/file/redirect")
    public void redirectUploadUrl(@RequestParam(value = "filename") String filename,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        Map<String, String> map = repository.createUploadUrl(filename,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId(),
                new HashMap<String, String>());
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        response.sendRedirect(map.get("url"));
    }

    @GetMapping("/upload/file/url")
    public ResponseDetails returnUploadUrl(@RequestParam(value = "filename") String filename,
                                           HttpServletRequest request) {
        Map<String, String> map = repository.createUploadUrl(filename,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId(),
                new HashMap<String, String>());
        return ResponseDetails.ok().put("data", map);
    }

    @PostMapping("/upload/file")
    public VditorFiles upload(@RequestParam(value = "file[]", required = false) MultipartFile[] files,
                              @RequestParam(value = "type", required = false) String type,
                              @RequestParam(value = "homework", required = false) Long homework,
                              HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(2);
        map.put("type", type);
        map.put("homework", String.valueOf(homework));
        VditorFiles vditorFiles = repository.save(files,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId(),
                map, request);
        return vditorFiles;
    }

    @GetMapping("/uploads/file/{userId}/{date}/{filename:.+}")
    public HttpEntity<?> getFile(@PathVariable("userId") String userId,
                                 @PathVariable("date") String date,
                                 @PathVariable("filename") String filename,
                                 HttpServletRequest request) {
        String path = "uploads/file/" + userId + "/" + date;
        String contentType = "application/octet-stream";
        try {
            Resource resource =resourceLoader.getResource("file:" + Paths.get(path, filename));
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            HttpHeaders headers = new HttpHeaders();
            // TODO AES key 验证
            if (FileUtil.PDF.equals(fileUtil.getFileSuffix(filename))) {
                headers.add("X-Frame-Options", "SAMEORIGIN");
            }
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename=" + filename);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType(contentType))
                    .headers(headers)
                    .body(resource);
        } catch (Exception ignored) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


    }
}
