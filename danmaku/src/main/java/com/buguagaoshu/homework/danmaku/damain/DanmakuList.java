package com.buguagaoshu.homework.danmaku.damain;

import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-07 22:05
 * 弹幕库列表
 * 前端需要解析的弹幕数据
 */
@Data
@Document("danmaku_list")
public class DanmakuList {
    /**
     * 查询ID
     * 关联值为课件 ID
     * */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    /**
     * 弹幕数据数组
     * */
    private List<List<Object>> data;
}
