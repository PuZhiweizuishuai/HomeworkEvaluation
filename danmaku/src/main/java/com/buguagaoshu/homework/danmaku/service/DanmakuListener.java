package com.buguagaoshu.homework.danmaku.service;

import com.buguagaoshu.homework.common.domain.DanmakuDetails;
import com.buguagaoshu.homework.danmaku.damain.Danmaku;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.Optional;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-09 19:54
 */
@Component
@Slf4j
public class DanmakuListener {
    private final DanmakuService danmakuService;

    private final ObjectMapper objectMapper;

    public DanmakuListener(DanmakuService danmakuService, ObjectMapper objectMapper) {
        this.danmakuService = danmakuService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"danmaku"})
    public void receiver(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            try {
                DanmakuDetails danmakuDetails = objectMapper.readValue(message.toString(), DanmakuDetails.class);
                Danmaku danmaku = new Danmaku();
                danmaku.setAuthor(danmakuDetails.getAuthor());
                danmaku.setColorDec(danmakuDetails.getColor());
                danmaku.setCoursewareId(danmakuDetails.getId());
                danmaku.setText(HtmlUtils.htmlEscape(danmakuDetails.getText()));
                danmaku.setColor(Long.toHexString(danmakuDetails.getColor()));
                danmaku.setTime(danmakuDetails.getTime());
                danmaku.setType(danmakuDetails.getType());
                danmaku.setCreateTime(System.currentTimeMillis());
                danmakuService.save(danmaku);
            } catch (Exception e) {
                log.error("弹幕数据反序列化失败：{}, 收到的信息为： {}, record: {}", e.getMessage(), message.toString(), record);
            }
        }
    }
}
