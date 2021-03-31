package com.buguagaoshu.homework.evaluation.model;

import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonSerializer;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-16 23:33
 */
@Data
public class ThinkModel {
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    /**
     * 正文
     */
    private String content;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 图片或正文
     */
    private List<String> files;

    /**
     * 转发ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long forward;

    /**
     * 转发量
     */
    private Long forwardCount;

    /**
     * 设备
     */
    private String ua;


    /**
     *
     */
    private Long createTime;

    /**
     *
     */
    private Long updateTime;

    /**
     *
     */
    private Long likeCount;

    /**
     *
     */
    private Long collectCount;

    /**
     *
     */
    private Integer status;

    /**
     *
     */
    private Long viewCount;


    private String authorId;


    private UserEntity user;


    private ArticleEntity article;


    private ThinkModel think;
}
