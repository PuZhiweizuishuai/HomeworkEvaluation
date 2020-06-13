package com.buguagaoshu.homework.evaluation.controller;


import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.model.VditorFiles;
import com.buguagaoshu.homework.evaluation.repository.FileStorageRepository;
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
 * TODO 文件格式验证
 */
@RestController
public class FileController {
    private final FileStorageRepository repository;

    private final ResourceLoader resourceLoader;

    @Autowired
    public FileController(FileStorageRepository repository, ResourceLoader resourceLoader) {
        this.repository = repository;
        this.resourceLoader = resourceLoader;
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
                              @RequestParam(value = "type", required = false) Map<String, String> type,
                              HttpServletRequest request) {

        VditorFiles vditorFiles = repository.save(files,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId(),
                type, request);
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
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "filename=" + filename)
                    .body(resource);
        } catch (Exception ignored) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


    }
}
