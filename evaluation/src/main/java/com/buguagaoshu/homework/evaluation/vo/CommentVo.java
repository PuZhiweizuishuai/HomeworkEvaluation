package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.common.domain.AtUser;
import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-29 21:09
 */
@Data
public class CommentVo {
    /**
     * 帖子ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @NotNull(message = "目标帖子ID不能为空")
    private Long articleId;


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
    @ListValue(value = {0, 1})
    private Integer type;

    /**
     * 回复的帖子ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long commentId;

    /**
     * 用户 @对象
     * */
    private List<AtUser> atUsers;

    private String verifyCode;
}
