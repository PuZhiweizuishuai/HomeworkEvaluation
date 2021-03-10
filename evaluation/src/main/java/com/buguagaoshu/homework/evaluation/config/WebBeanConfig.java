package com.buguagaoshu.homework.evaluation.config;

import com.buguagaoshu.homework.common.utils.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public WebConstant webConstant() {
        return new WebConstant();
    }
}
