package com.buguagaoshu.homework.binlogcanal.utils;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.buguagaoshu.homework.common.enums.TableEventType;
import com.buguagaoshu.homework.common.search.SearchTableNameConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 13:14
 */
public class CreateSearchObjectUtils {
    private final ObjectMapper objectMapper;

    public CreateSearchObjectUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String objectToJson(List<CanalEntry.Column> columns, String tableName, TableEventType tableEventType) {
        Map<String, String> map = new HashMap<>();
        for (CanalEntry.Column column : columns) {
            if (StringUtils.containsIgnoreCase(column.getMysqlType(), "BLOB")
                    || StringUtils.containsIgnoreCase(column.getMysqlType(), "BINARY")) {
                // get value bytes
                map.put(column.getName(), new String(column.getValue().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            } else {
                map.put(column.getName(), column.getValue());
            }
        }
        map.put(SearchTableNameConstant.EVENT_TYPE_KEY, String.valueOf(tableEventType.getCode()));
        map.put(SearchTableNameConstant.TABLE_NAME_KEY, tableName);
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
