package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.common.domain.AtUser;
import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-29 15:59
 */
@Data
public class ArticleVo {
    @NotBlank(message = "帖子标题不能为空！")
    @Length(min = 1, max = 50, message = "帖子标题最长不能超过50个字符！")
    private String title;

    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long tagId;


    private List<String> tag;


    @NotBlank(message = "帖子正文不能为空！")
    private String content;


    @ListValue(value = {-1, 0, 1, 2, 10, 11, 12, 200}, message = "帖子类型取值不对！")
    private Integer type;


    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long courseId;

    @Max(value = 50, message = "简介不能超过50个字！")
    private String simpleContent;


    @NotNull(message = "验证码不能为空!")
    private String verifyCode;


    private List<AtUser> atUsers;


    private List<String> files;

    /**
     * 问答悬赏积分（仅作用于问答帖）
     */
    private Integer offerPoint;

    /**
     * 课程评分
     * */
    private Double courseRating;

    /**
     * 投票数据
     * */
    private List<VoteVo> votes;

    /**
     * 转发
     * */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long forward;

    private Long forwardCount;
}
