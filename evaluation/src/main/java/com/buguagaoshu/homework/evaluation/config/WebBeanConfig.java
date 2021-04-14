package com.buguagaoshu.homework.evaluation.config;

import com.buguagaoshu.homework.common.utils.FileUtil;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-04 22:41
 */
@Configuration
public class WebBeanConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public FileUtil fileUtil() {
        return new FileUtil();
    }

    @Bean
    public IpUtil ipUtil(MinioClient minioClient, MinIOConfigProperties properties) {
        return new IpUtil(minioClient, properties);
    }

    @Bean
    public WebConstant webConstant() {
        return new WebConstant();
    }
}
