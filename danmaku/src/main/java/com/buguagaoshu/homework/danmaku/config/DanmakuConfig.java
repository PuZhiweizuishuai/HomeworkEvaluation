package com.buguagaoshu.homework.danmaku.config;

import com.buguagaoshu.homework.danmaku.util.BuildBackInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-06 23:55
 */
@Configuration
public class DanmakuConfig {
    @Bean
    public BuildBackInfo buildBackInfo() {
        return new BuildBackInfo();
    }
}
