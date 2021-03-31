package com.buguagaoshu.homework.binlogcanal.config;


import com.buguagaoshu.homework.binlogcanal.utils.CreateSearchObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 13:15
 */
@Configuration
public class WebConfig {
    @Bean
    public CreateSearchObjectUtils articleUtils() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new CreateSearchObjectUtils(objectMapper);
    }
}
