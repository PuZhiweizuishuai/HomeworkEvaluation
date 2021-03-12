package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import com.buguagaoshu.homework.common.valid.ListValue;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 帖子表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-28 20:13:56
 */
@Data
@TableName("article")
public class ArticleEntity {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子标题
     */
    @NotBlank(message = "帖子标题不能为空！")
    @Length(min = 1, max = 50, message = "帖子标题最长不能超过50个字符！")
    private String title;

    /**
     * 分区ID
     */
    private Integer tagId;

    /**
     * 帖子标签，英文状态逗号分隔
     */
    private String tag;

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
    @NotBlank(message = "帖子正文不能为空！")
    private String content;


    private String simpleContent;

    /**
     * '@' 用户
     * */
    private String atUser;

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
    @ListValue(value = {0, 1, 2, 3}, message = "帖子类型取值不对！")
    private Integer type;

    /**
     * 课程ID
     */
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

    /**
     * 发帖 IP
     */
    private String ip;

    /**
     * User-Agent
     */
    private String ua;

    /**
     * 帖子置顶时间
     */
    private Long articlestick;

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
    private String topImgUrl;


    /**
     * 课程评分
     * */
    private Double courseRating;


    public void initData() {
        long time = System.currentTimeMillis();
        this.setCommentCount(0L);
        this.setViewCount(0L);
        this.setCreateTime(time);
        this.setUpdateTime(time);
        this.setLatestCommentName(null);
        this.setLatestCommentTime(null);
        this.setStatus(0);
        this.setLikeCount(0L);
        this.setBadCount(0L);
        this.setCollectCount(0L);
        this.setArticlestick(0L);
        this.setAnonymous(0);
        this.setPerfect(0);
        this.setQAOfferPoint(null);
    }
}
