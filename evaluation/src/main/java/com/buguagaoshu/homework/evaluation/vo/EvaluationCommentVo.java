package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-01 23:11
 * 作业互评评价信息
 */
@Data
public class EvaluationCommentVo {
    /**
     * 帖子ID
     */
    @NotNull(message = "目标作业ID不能为空")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long submitId;


    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long fatherId;

    /**
     * 回复正文
     */
    @NotBlank(message = "评论正文不能为空！")
    private String content;

    /**
     * 作者ID
     */
    private String authorId;

    /**
     * 0 一级回复， 1 回复评论
     */
    @ListValue(value = {10, 11})
    private Integer type;

    /**
     * 回复的帖子ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long commentId;


    private String verifyCode;

    private Double rating;
}
