package com.buguagaoshu.homework.danmaku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.buguagaoshu.homework.danmaku.feign")
public class DanmakuApplication {

    public static void main(String[] args) {
        SpringApplication.run(DanmakuApplication.class, args);
    }

}
