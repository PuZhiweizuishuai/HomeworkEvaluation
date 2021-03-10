package com.buguagaoshu.homework.evaluation.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-27 19:13
 */
@RefreshScope
@Data
public class WebConstant {
    /**
     * AES 加密 KEY
     * */
    @Value("${Aes.key}")
    private String aesKey;


    /**
     * AES 加密时效
     * 默认 6 小时
     * */
    public final static Long AES_EXPIRES_TIME = 21600000L;



    public final static String VERIFY_CODE_KEY = "verifyCodeKey";

}
