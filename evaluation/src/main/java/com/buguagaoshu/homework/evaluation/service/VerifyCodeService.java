package com.buguagaoshu.homework.evaluation.service;

import com.buguagaoshu.homework.evaluation.entity.UserEntity;

import java.awt.*;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2019-11-26 17:05
 * 验证码验证接口
 */
public interface VerifyCodeService {

    String randomDigitString(int length);

    void send(String key, UserEntity userEntity);

    void verify(String key, String code);

    Image image(String key);
}
