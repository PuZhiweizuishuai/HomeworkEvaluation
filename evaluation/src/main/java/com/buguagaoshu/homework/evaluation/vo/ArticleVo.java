package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.common.domain.AtUser;
import com.buguagaoshu.homework.common.valid.ListValue;
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

    private Integer tagId;


    private List<String> tag;


    @NotBlank(message = "帖子正文不能为空！")
    private String content;


    @ListValue(value = {0, 1, 2, 3, 4}, message = "帖子类型取值不对！")
    private Integer type;


    private Long courseId;

    @Max(value = 50, message = "简介不能超过50个字！")
    private String simpleContent;


    @NotNull(message = "验证码不能为空!")
    private String verifyCode;


    private List<AtUser> atUsers;

    /**
     * 问答悬赏积分（仅作用于问答帖）
     */
    private Integer offerPoint;

    /**
     * 课程评分
     * */
    private Double courseRating;
}
