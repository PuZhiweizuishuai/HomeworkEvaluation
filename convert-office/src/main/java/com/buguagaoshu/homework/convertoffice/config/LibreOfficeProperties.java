package com.buguagaoshu.homework.convertoffice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 15:31
 */
@ConfigurationProperties(prefix = "libreoffice")
@Component
@Data
public class LibreOfficeProperties {
    private String home;

    private List<Integer> ports;


    private Long taskExecutionTimeoutMinutes;


    private Long taskQueueTimeoutHours;
}
