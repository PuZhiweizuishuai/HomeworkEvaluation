package com.buguagaoshu.homework.convertoffice.repository.impl;

import com.buguagaoshu.homework.convertoffice.config.FileConstant;
import com.buguagaoshu.homework.convertoffice.config.MinIOConfigProperties;
import com.buguagaoshu.homework.convertoffice.repository.FileStorageRepository;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 20:34
 */
@Repository
@Slf4j
public class MinIoRepository implements FileStorageRepository {
    private final MinioClient minioClient;

    private final MinIOConfigProperties properties;

    @Autowired
    public MinIoRepository(MinioClient minioClient, MinIOConfigProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public boolean downloadFileToTemp(String filPath, String filename) {
        try {
            Files.copy(minioClient.getObject(properties.getBucketName(), filPath), Paths.get(FileConstant.TEMP, filename));
            return true;
        } catch (Exception e) {
            log.error("文件 {} 下载失败，失败原因：\n {}", filPath, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean uploadFile(String localFilePath, String ossPath) {
        try {
            minioClient.putObject(properties.getBucketName(), ossPath, localFilePath);
            return true;
        } catch (Exception e) {
            log.error("文件 {} 上传失败，失败原因： \n {}", localFilePath, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteTempFile(String filePath) {
        try {
            Files.delete(Paths.get(filePath));
            return true;
        } catch (Exception e) {
            log.error("文件 {} 删除失败, 失败原因： \n {}", filePath, e.getMessage());
            return false;
        }


    }
}
