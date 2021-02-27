package com.buguagaoshu.homework.binlogcanal.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-02-28 0:05
 */
@Component
@Slf4j
public class ObjectToJsonUtil {
    private final ObjectMapper objectMapper;

    @Autowired
    public ObjectToJsonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("对象 {} 序列化失败!, {}", object.getClass().getName(), e.getMessage());
            return null;
        }
    }
}
