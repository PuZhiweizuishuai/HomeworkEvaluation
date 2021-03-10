package com.buguagaoshu.homework.convertoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConvertOfficeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvertOfficeApplication.class, args);
    }

}
