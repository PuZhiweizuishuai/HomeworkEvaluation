package com.buguagaoshu.homework.oss.repository.impl;

import com.buguagaoshu.homework.common.domain.FileModel;
import com.buguagaoshu.homework.common.domain.VditorFiles;
import com.buguagaoshu.homework.common.utils.FileUtil;
import com.buguagaoshu.homework.oss.config.MinIOConfigProperties;
import com.buguagaoshu.homework.oss.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.oss.repository.FileStorageRepository;
import com.buguagaoshu.homework.oss.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 17:07
 */
@Repository
@Slf4j
public class MinIoRepository implements FileStorageRepository {
    private final MinioClient minioClient;

    private final MinIOConfigProperties minIOConfigProperties;

    private final FileUtil fileUtil;

    @Autowired
    public MinIoRepository(MinioClient minioClient, MinIOConfigProperties minIOConfigProperties, FileUtil fileUtil) {
        this.minioClient = minioClient;
        this.minIOConfigProperties = minIOConfigProperties;
        this.fileUtil = fileUtil;
    }


    @Override
    public VditorFiles save(MultipartFile[] files, String userId, Map<String, String> params, HttpServletRequest request) {
        VditorFiles vditorFiles = new VditorFiles();
        vditorFiles.setCode(1);
        List<String> errFiles = new ArrayList<>();
        if (files.length > FileUtil.ONCE_UPLOAD_FILE_NUMBER) {
            for (MultipartFile f : files) {
                errFiles.add(f.getOriginalFilename());
            }
            vditorFiles.setMsg("一次最多上传文件不能超过9个！");
            Map<String, Object> data = new HashMap<>(2);
            data.put("errFiles", errFiles);
            vditorFiles.setData(data);
            return vditorFiles;
        }
        boolean isFlag = false;
        // 检查上传文件大小

        for (MultipartFile f : files) {
            if (f.getSize() > FileUtil.MAX_FILE_SIZE) {
                isFlag = true;
                vditorFiles.setMsg("单个文件大小最大不能超过 20M！");
            }
            errFiles.add(f.getOriginalFilename());
        }
        if (isFlag) {
            Map<String, Object> data = new HashMap<>(2);
            data.put("errFiles", errFiles);
            vditorFiles.setData(data);
            return vditorFiles;
        }

        // 检查文件上传格式
        // TODO 如果时头像，格式必须是图片
        // TODO 检查上传作业时的上传权限
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (!fileUtil.checkSuffix(fileName)) {
                vditorFiles.setMsg("不支持的文件格式！");
                return vditorFiles;
            }
        }

        // 处理文件保存
        Map<String, Object> succMap = new HashMap<>(2);
        errFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            FileModel fileModel = fileUtil.filePath(fileName, userId, params.get("type"), params.get("homework"));

            String pathName = fileModel.getPath() + "/" + fileModel.getFilename();
            try {
                // 保存
                minioClient.putObject(
                        PutObjectArgs.builder().bucket(minIOConfigProperties.getBucketName()).object(pathName).stream(
                                file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build());
                succMap.put(fileName, "/api/" + pathName);
            } catch (Exception e) {
                errFiles.add(fileName);
            }
        }
        Map<String, Object> data = new HashMap<>(2);
        vditorFiles.setCode(0);
        vditorFiles.setMsg("上传成功");
        data.put("succMap", succMap);
        data.put("errFiles", errFiles);
        vditorFiles.setData(data);
        return vditorFiles;
    }

    @Override
    public Map<String, String> save(MultipartFile file, String course, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        FileModel fileModel = fileUtil.coursewareFilePath(file.getOriginalFilename(), course, user.getId());
        String pathName = fileModel.getPath() + "/" + fileModel.getFilename();
        File dest = new File(fileModel.getPath());
        HashMap<String, String> map = new HashMap<>(2);
        //判断文件父目录是否存在
        if (!dest.exists() && !dest.mkdirs()) {
            return null;
        }
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(minIOConfigProperties.getBucketName()).object(pathName).stream(
                            file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            map.put("filename", file.getOriginalFilename());
            map.put("path", "/api/" + pathName);
        } catch (Exception e) {
            log.error("教师 {} 上传课件： {} 出现异常: {}", user.getId(), file.getOriginalFilename(), e.getMessage());
            return null;
        }
        return map;
    }

    @Override
    public String getFileUrl(String path) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minIOConfigProperties.getBucketName())
                            .object(path)
                            .expiry(6, TimeUnit.HOURS)
                            .build()

            );
        } catch (Exception e) {
            // 返回 404
            return "";
        }
    }

    @Override
    public Boolean delete(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minIOConfigProperties.getBucketName())
                            .object(path)
                            .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
