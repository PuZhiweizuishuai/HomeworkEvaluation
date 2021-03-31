package com.buguagaoshu.homework.search.listener;

import com.buguagaoshu.homework.search.serivce.SynchronizeDataService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final SynchronizeDataService synchronizeDataService;

    @Autowired
    public SaveDataToEsListener(SynchronizeDataService synchronizeDataService) {
        this.synchronizeDataService = synchronizeDataService;
    }

    @KafkaListener(topics = {"searchDataSynchronize"})
    public void receiver(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        // 具体操作
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            synchronizeDataService.save(message.toString());
        }
    }
}
