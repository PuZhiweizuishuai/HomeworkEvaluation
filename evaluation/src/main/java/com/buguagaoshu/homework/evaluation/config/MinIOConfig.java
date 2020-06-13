package com.buguagaoshu.homework.evaluation.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-04-08 21:51
 */
@Configuration
public class MinIOConfig {
    private final MinIOConfigProperties minIOConfigProperties;

    @Autowired
    public MinIOConfig(MinIOConfigProperties minIOConfigProperties) {
        this.minIOConfigProperties = minIOConfigProperties;
    }

    @Bean
    public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(minIOConfigProperties.getServer(), minIOConfigProperties.getAccessKey(), minIOConfigProperties.getSecretKey());
    }
}
