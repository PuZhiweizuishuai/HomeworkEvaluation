package com.buguagaoshu.homework.convertoffice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-04-08 21:46
 * 获取 MinIO 配置属性
 */
@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinIOConfigProperties {
    /**
     * 服务器地址
     * */
    private String server;

    /**
     * 账号
     * */
    private String accessKey;

    /**
     * 密码
     * */
    private String secretKey;

    /**
     * 存储桶名称
     * */
    private String bucketName;

    /**
     * 生成上传 URL 的有效期（以秒为单位）
     * */
    private Integer expiry;

}
