package com.buguagaoshu.homework.evaluation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-03 23:59
 * 网站基础信息
 */
@Component
@ConfigurationProperties(prefix = "web.base-info")
@Data
public class BaseWebInfoConfig {
    /**
     * 网站名
     * */
    private String webName;

    /**
     * 网站 Logo 地址
     * */
    private String logoUrl;

    /**
     * 网站 favicon 图标地址
     * */
    private String faviconUrl;

    /**
     * 文件上传服务商
     * 默认 0 = MinIO
     *     1 = 本地存储
     * */
    private Integer uploadFileService = 0;


    private String website = "http://127.0.0.1:8080";

    private String fileSuffix;
}
