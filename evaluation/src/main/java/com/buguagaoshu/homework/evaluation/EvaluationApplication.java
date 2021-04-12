package com.buguagaoshu.homework.evaluation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Pu Zhiwei
 * */
@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.buguagaoshu.homework.evaluation.feign")
public class EvaluationApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvaluationApplication.class, args);
    }

}
