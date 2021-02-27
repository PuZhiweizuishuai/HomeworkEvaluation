package com.buguagaoshu.homework.binlogcanal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-02-27 23:27
 */
@ConfigurationProperties(prefix = "alibaba.canal")
@Component
@Data
public class AlibabaCanalProperties {
    private String[] host;

    private Integer port;

    private String username;

    private String password;

    private String destination;

    private String database;
}
