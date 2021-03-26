package com.buguagaoshu.homework.search.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-25 18:33
 * 监听数据更新，同步保存到ES
 */
@Component
public class SaveDataToEsListener {
    @KafkaListener(topics = {"Search"})
    public void receiver(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        // 具体操作
        if (kafkaMessage.isPresent()) {

        }
    }
}
