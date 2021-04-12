package com.buguagaoshu.homework.evaluation.service;

import com.buguagaoshu.homework.evaluation.entity.UserEntity;

/**
 * @author puzhiwei
 * 发送验证消息
 * */
public interface SendMessageService {

    void send(String key, String message, UserEntity userEntity);
}
