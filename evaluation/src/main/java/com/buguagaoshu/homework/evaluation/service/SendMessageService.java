package com.buguagaoshu.homework.evaluation.service;

import com.buguagaoshu.homework.common.domain.MailDetails;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;

/**
 * @author puzhiwei
 * 发送验证消息
 * */
public interface SendMessageService {

    void sendVerifyCode(String key, String message, UserEntity userEntity, String email);

    void send(MailDetails mailDetails);
}
