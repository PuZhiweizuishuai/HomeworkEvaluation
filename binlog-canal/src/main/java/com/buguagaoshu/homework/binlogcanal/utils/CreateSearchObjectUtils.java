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
                map.put(toCamelCase(column.getName()), new String(column.getValue().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            } else {
                map.put(toCamelCase(column.getName()), column.getValue());
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

    public String toCamelCase(String underlineStr) {
        if (underlineStr == null) {
            return null;
        }
        // 分成数组
        char[] charArray = underlineStr.toCharArray();
        // 判断上次循环的字符是否是"_"
        boolean underlineBefore = false;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, l = charArray.length; i < l; i++) {
            // 判断当前字符是否是"_",如果跳出本次循环
            if (charArray[i] == 95) {
                underlineBefore = true;
            } else if (underlineBefore) {
                // 如果为true，代表上次的字符是"_",当前字符需要转成大写
                buffer.append(charArray[i] -= 32);
                underlineBefore = false;
            } else {
                // 不是"_"后的字符就直接追加
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }
}
