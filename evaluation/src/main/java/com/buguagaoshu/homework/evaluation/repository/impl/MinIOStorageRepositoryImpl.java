package com.buguagaoshu.homework.evaluation.repository.impl;

import com.buguagaoshu.homework.evaluation.config.MinIOConfigProperties;
import com.buguagaoshu.homework.evaluation.exception.FilePathException;
import com.buguagaoshu.homework.evaluation.model.FileModel;
import com.buguagaoshu.homework.evaluation.model.VditorFiles;
import com.buguagaoshu.homework.evaluation.repository.FileStorageRepository;
import com.buguagaoshu.homework.evaluation.utils.FileUtil;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-04-08 21:29
 * MinIO 对象存储实现
 * 实际部署可以采用如阿里云OSS等其它云服务的OSS服务
 */
//@Repository
@Slf4j
public class MinIOStorageRepositoryImpl implements FileStorageRepository {
    private final MinioClient minioClient;

    private final MinIOConfigProperties properties;

    private final FileUtil fileUtil;

    @Autowired
    public MinIOStorageRepositoryImpl(MinioClient minioClient, MinIOConfigProperties properties, FileUtil fileUtil) {
        this.minioClient = minioClient;
        this.properties = properties;
        this.fileUtil = fileUtil;
    }

    @Override
    public Map<String, String> createUploadUrl(String objectName, String userId, Map<String, String> params) {
        try {
            FileModel fileModel = fileUtil.filePath(objectName, userId, params.get("type"), params.get("homework"));
            String filename = fileModel.getPath() + "/" + fileModel.getFilename();
            String url = minioClient.presignedPutObject(properties.getBucketName(), filename, properties.getExpiry());
            HashMap<String, String> map = new HashMap<>(2);
            map.put("url", url);
            map.put("filename", filename);
            return map;
        } catch(Exception e) {
            log.error("Error occurred:  {}", e.getMessage());
            throw new FilePathException("文件服务器链接异常");
        }
    }

    /**
     * 转发上传
     * */
    @Override
    public VditorFiles save(MultipartFile[] files,
                            String userId,
                            Map<String, String> params,
                            HttpServletRequest request) {
        return null;
    }

    @Override
    public Path load(String path, String filename) throws FileNotFoundException {
        return null;
    }
}
