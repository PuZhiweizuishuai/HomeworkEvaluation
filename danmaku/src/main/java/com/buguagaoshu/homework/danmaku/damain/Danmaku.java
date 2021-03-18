package com.buguagaoshu.homework.danmaku.damain;

import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-06 22:35
 * 保存单一的弹幕数据包含详细数据
 */
@Data
@Document("danmaku")
public class Danmaku {
    @MongoId
    private ObjectId id;

    /**
     * 课件ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long coursewareId;

    /**
     * 弹幕颜色
     */
    private String color;


    private String author;

    /**
     * 弹幕内容
     */
    private String text;

    /**
     * 时间
     */
    private Double time;

    /**
     * 类型
     */
    private Integer type;


    /**
     * 十进制颜色数据
     * */
    private Long colorDec;


    private Long createTime;
}
