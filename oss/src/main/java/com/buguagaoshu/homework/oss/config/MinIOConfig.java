package com.buguagaoshu.homework.oss.config;

import com.buguagaoshu.homework.common.utils.FileUtil;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
    public MinioClient minioClient() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minIOConfigProperties.getServer())
                        .credentials(minIOConfigProperties.getAccessKey(), minIOConfigProperties.getSecretKey())
                        .build();
        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket(minIOConfigProperties.getBucketName()).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minIOConfigProperties.getBucketName()).build());
        }
        return minioClient;
    }


    @Bean
    public CommandLineRunner dataLoader(MinioClient minioClient) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                //
            }
        };
    }
}
