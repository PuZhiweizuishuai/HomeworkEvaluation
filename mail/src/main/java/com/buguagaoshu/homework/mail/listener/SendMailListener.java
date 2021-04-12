package com.buguagaoshu.homework.mail.listener;

import com.buguagaoshu.homework.common.domain.MailDetails;
import com.buguagaoshu.homework.mail.service.SendMailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-12 20:39
 * 监听邮件发送请求
 */
@Component
@Slf4j
public class SendMailListener {
    private final ObjectMapper objectMapper;

    private final SendMailService sendMailService;

    @Autowired
    public SendMailListener(ObjectMapper objectMapper, SendMailService sendMailService) {
        this.objectMapper = objectMapper;
        this.sendMailService = sendMailService;
    }

    @KafkaListener(topics = {"mail"})
    public void receiver(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            String message = (String) kafkaMessage.get();
            try {
                MailDetails mailDetails = objectMapper.readValue(message, MailDetails.class);
                sendMailService.send(mailDetails);
            } catch (Exception e) {
                log.error("邮件发送异常： {}", e.getMessage());
            }
        }
    }
}
