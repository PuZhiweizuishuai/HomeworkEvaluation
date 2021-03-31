package com.buguagaoshu.homework.common.search;


import com.buguagaoshu.homework.common.utils.DoubleDeserializer;
import com.buguagaoshu.homework.common.utils.IntJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 12:57
 */
public class ArticleSearchMapper {
    private Long id;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 分区ID
     */
    private Long tagId;

    /**
     * 帖子标签，英文状态逗号分隔
     */
    private String tag;

    private String authorId;

    /**
     * 作者名
     */
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
     * '@' 用户
     */
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
    private String files;


    /**
     * 课程评分
     */
    private Double courseRating;

    /**
     * 转发
     */
    private Long forward;


    private Long forwardCount;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSimpleContent() {
        return simpleContent;
    }

    public void setSimpleContent(String simpleContent) {
        this.simpleContent = simpleContent;
    }

    public String getAtUser() {
        return atUser;
    }

    public void setAtUser(String atUser) {
        this.atUser = atUser;
    }

    public String getPermaLink() {
        return permaLink;
    }

    public void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getLatestCommentTime() {
        return latestCommentTime;
    }

    public void setLatestCommentTime(Long latestCommentTime) {
        this.latestCommentTime = latestCommentTime;
    }

    public String getLatestCommentName() {
        return latestCommentName;
    }

    public void setLatestCommentName(String latestCommentName) {
        this.latestCommentName = latestCommentName;
    }

    public Integer getCommentable() {
        return commentable;
    }

    public void setCommentable(Integer commentable) {
        this.commentable = commentable;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getBadCount() {
        return badCount;
    }

    public void setBadCount(Long badCount) {
        this.badCount = badCount;
    }

    public Long getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Long collectCount) {
        this.collectCount = collectCount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public Long getArticlestick() {
        return articlestick;
    }

    public void setArticlestick(Long articlestick) {
        this.articlestick = articlestick;
    }

    public Integer getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Integer anonymous) {
        this.anonymous = anonymous;
    }

    public Integer getPerfect() {
        return perfect;
    }

    public void setPerfect(Integer perfect) {
        this.perfect = perfect;
    }

    public Integer getqAOfferPoint() {
        return qAOfferPoint;
    }

    public void setqAOfferPoint(Integer qAOfferPoint) {
        this.qAOfferPoint = qAOfferPoint;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public Double getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(Double courseRating) {
        this.courseRating = courseRating;
    }

    public Long getForward() {
        return forward;
    }

    public void setForward(Long forward) {
        this.forward = forward;
    }

    public Long getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(Long forwardCount) {
        this.forwardCount = forwardCount;
    }
}
