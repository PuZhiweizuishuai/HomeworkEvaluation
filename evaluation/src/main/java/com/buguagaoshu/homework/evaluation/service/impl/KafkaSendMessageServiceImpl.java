package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.config.CustomConstant;
import com.buguagaoshu.homework.common.domain.MailDetails;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.service.SendMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author puzhiwei
 */
@Service
@Slf4j
public class KafkaSendMessageServiceImpl implements SendMessageService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaSendMessageServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public void send(String key, String message, UserEntity userEntity) {
        log.info("Send to key:[{}] with message:[{}]", key, message);
        MailDetails mailDetails = new MailDetails();
        mailDetails.setCode(message);
        mailDetails.setTitle("作业互评验证码");
        mailDetails.setTo(userEntity.getEmail());
        mailDetails.setName(userEntity.getUsername());
        mailDetails.setText("");
        mailDetails.setType(CustomConstant.VERIFY_CODE_MAIL);
        try {
            kafkaTemplate.send("mail", objectMapper.writeValueAsString(mailDetails));
        } catch (Exception e) {
            log.error("Kafka发送消息失败： {}", e.getMessage());
            throw new UserDataFormatException("邮件发送失败，请联系管理员或者稍后再试！");
        }

    }
}
