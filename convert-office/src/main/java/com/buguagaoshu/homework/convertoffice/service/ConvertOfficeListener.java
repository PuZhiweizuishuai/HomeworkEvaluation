package com.buguagaoshu.homework.convertoffice.service;

import com.buguagaoshu.homework.common.domain.ConvertOfficeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * 监听转换请求
 * create          2021-03-10 15:44
 */
@Component
@Slf4j
public class ConvertOfficeListener {

    private final ObjectMapper objectMapper;

    private final ConvertOfficeService convertOfficeService;

    @Autowired
    public ConvertOfficeListener(ObjectMapper objectMapper, ConvertOfficeService convertOfficeService) {
        this.objectMapper = objectMapper;
        this.convertOfficeService = convertOfficeService;
    }

    @KafkaListener(topics = {"ConvertOffice"})
    public void receiver(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            try {
                ConvertOfficeInfo info = objectMapper.readValue(message.toString(), ConvertOfficeInfo.class);
                convertOfficeService.ConvertOfficeToPDF(info);
            } catch (Exception e) {
                log.error("接收消息队列转换命令异常！收到信息内容：{}, 异常原因: \n {}", message, e.getMessage());
            }
        }
    }
}
