package com.buguagaoshu.homework.search.serivce;

import com.buguagaoshu.homework.common.enums.TableEventType;
import com.buguagaoshu.homework.common.search.SearchTableNameConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 15:02
 */
@Service
@Slf4j
public class SynchronizeDataService {
    private final ObjectMapper objectMapper;

    @Autowired
    public SynchronizeDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void save(String message) {
        HashMap<String, String> map = jsonToMap(message);
        if (map != null) {
            // 获取操作类型
            int eventType = Integer.parseInt(map.get(SearchTableNameConstant.EVENT_TYPE_KEY));
            // 获取表名
            String tableName = map.get(SearchTableNameConstant.TABLE_NAME_KEY);
            // 处理不同操作
            System.out.println(map);
            if (eventType == TableEventType.INSERT.getCode()) {
                insert(map, tableName);
            } else if (eventType == TableEventType.UPDATE.getCode()) {
                update(map, tableName);
            } else if (eventType == TableEventType.DELETE.getCode()) {
                delete(map, tableName);
            }
        }
    }

    public void insert(Map<String, String> map, String tableName) {

    }

    public void delete(Map<String, String> map, String tableName) {

    }

    public void update(Map<String, String> map, String tableName) {

    }


    private HashMap<String, String> jsonToMap(String json) {
        try {
            return objectMapper.readValue(json, HashMap.class);
        } catch (JsonProcessingException e) {
            log.error("json数据 {} 转换失败！,失败原因：{}", json, e.getMessage());
            return null;
        }
    }
}
