package com.buguagaoshu.homework.evaluation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Pu Zhiwei
 * */
@EnableDiscoveryClient
@SpringBootApplication
public class EvaluationApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvaluationApplication.class, args);
    }

}
