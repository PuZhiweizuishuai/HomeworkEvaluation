package com.buguagaoshu.homework.evaluation.model;


import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.entity.VoteEntity;
import com.buguagaoshu.homework.evaluation.entity.VoteLogEntity;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-29 19:14
 * 返回帖子数据
 */
@Data
public class ArticleModel {
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 分区ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long tagId;

    /**
     * 帖子标签，英文状态逗号分隔
     */
    private List<String> tag;

    /**
     * 帖子作者 id
     */
    private String authorId;

    /**
     * 作者名
     * */
    private String authorName;

    /**
     * 帖子回帖计数
     */
    private Long commentCount;

    /**
     * 帖子浏览计数
     */
    private Long viewCount;

    /**
     * 帖子正文内容
     */
    private String content;


    private String simpleContent;

    /**
     * 帖子访问路径
     */
    private String permaLink;

    /**
     * 帖子创建时间
     */
    private Long createTime;

    /**
     * 帖子更新时间
     */
    private Long updateTime;

    /**
     * 帖子最新回帖时间
     */
    private Long latestCommentTime;

    /**
     * 帖子最新回帖者用户名
     */
    private String latestCommentName;

    /**
     * 帖子是否可回帖[0 不行， 1 可以]
     */
    private Integer commentable;

    /**
     * 0：正常，1：锁定，   2删除
     */
    private Integer status;

    /**
     * 0：普通帖子，1：课程讨论贴，2，问答贴， 3，想法
     */
    private Integer type;

    /**
     * 课程ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long courseId;

    /**
     * 帖子点赞计数
     */
    private Long likeCount;

    /**
     * 帖子点踩计数
     */
    private Long badCount;

    /**
     * 帖子收藏计数
     */
    private Long collectCount;


    private Boolean hasCollectStatus;


    /**
     * User-Agent
     */
    private String ua;

    /**
     * 帖子置顶时间
     */
    private Long articlestick;

    private String atUser;

    /**
     * 0：公开，1：匿名
     */
    private Integer anonymous;

    /**
     * 0：非精品，1：精品
     */
    private Integer perfect;

    /**
     * 问答悬赏积分（仅作用于问答帖）
     */
    private Integer qAOfferPoint;

    /**
     * 帖子首图地址
     */
    private String files;

    private UserEntity user;

    private String avatarUrl;


    private Boolean isTeacher;

    /**
     * 课程评分
     * */
    private Double courseRating;


    private List<VoteMode> votes;


    private VoteLogEntity voteLog;

    /**
     * 转发
     * */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long forward;

    private Long forwardCount;
}
