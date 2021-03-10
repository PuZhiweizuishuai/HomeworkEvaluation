package com.buguagaoshu.homework.convertoffice.config;

import com.buguagaoshu.homework.common.utils.FileUtil;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-04-08 21:51
 */
@Configuration
public class MinIOConfig {
    private final MinIOConfigProperties minIOConfigProperties;


    @Bean
    public FileUtil fileUtil() {
        return new FileUtil();
    }

    @Autowired
    public MinIOConfig(MinIOConfigProperties minIOConfigProperties) {
        this.minIOConfigProperties = minIOConfigProperties;
    }

    @Bean
    public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(minIOConfigProperties.getServer(), minIOConfigProperties.getAccessKey(), minIOConfigProperties.getSecretKey());
    }


    @Bean
    public CommandLineRunner dataLoader(MinioClient minioClient) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                // 初始化检查
                boolean found = minioClient.bucketExists(minIOConfigProperties.getBucketName());
                // 如果不存在，则创建一个存储桶
                if (!found) {
                    minioClient.makeBucket(minIOConfigProperties.getBucketName());
                }
            }
        };
    }
}
