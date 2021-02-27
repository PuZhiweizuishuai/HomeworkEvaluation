package com.buguagaoshu.homework.binlogcanal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BinlogCanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinlogCanalApplication.class, args);
    }

}
