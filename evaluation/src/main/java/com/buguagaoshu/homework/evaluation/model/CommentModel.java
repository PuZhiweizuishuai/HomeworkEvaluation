package com.buguagaoshu.homework.evaluation.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-29 21:15
 */
@Data
public class CommentModel {
    /**
     * 回帖主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子ID
     */
    private Long articleId;

    private Long fatherId;

    /**
     * 回复正文
     */
    private String content;

    /**
     * 作者ID
     */
    private String authorId;

    /**
     * 0 一级回复， 1 回复评论
     */
    private Integer type;

    /**
     * 回复的帖子ID
     */
    private Long commentId;

    /**
     * 状态 0 正常， 1 删除
     */
    private Integer status;


    /**
     * 回复设备UA
     */
    private String ua;

    /**
     * 喜欢数
     */
    private Long likeCount;

    private Long commentCount;

    /**
     * 不喜欢数
     */
    private Long badCount;

    /**
     * 回复是否被采纳，用于问答贴，0 未采纳， 1被采纳
     */
    private Integer qAOffered;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;


    private String username;


    private String avatarUrl;


    private Boolean isTeacher;


    private String replyUserId;

    private String replyUserName;

    private String replyUserAvatar;
}
