package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.common.valid.ListValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-29 21:09
 */
@Data
public class CommentVo {
    /**
     * 帖子ID
     */
    @NotNull(message = "目标帖子ID不能为空")
    private Long articleId;

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
    @ListValue(value = {0, 1})
    private Integer type;

    /**
     * 回复的帖子ID
     */
    private Long commentId;

    private String verifyCode;
}
