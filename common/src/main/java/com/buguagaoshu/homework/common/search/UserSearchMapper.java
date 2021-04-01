package com.buguagaoshu.homework.common.search;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-01 12:23
 */
public class UserSearchMapper {
    private String userId;

    /**
     * 姓名
     */
    private String username;


    /**
     * 【0 男，1 女】
     */
    private Integer sex;

    /**
     * 生日 yyyy-MM-dd
     */
    private String birthday;
    /**
     * 头像地址
     */
    private String userAvatarUrl;

    /**
     * 个人主页顶部大图
     */
    private String topImgUrl;

    /**
     * 用户简介
     */
    private String userIntro;
    /**
     * 加入时间
     */
    private Long createTime;

    /**
     * 关注者数量
     */
    private Long followerCount;
    /**
     * 粉丝数量
     */
    private Long fansCount;

    private String school;
    /**
     * 专业
     */
    private String major;
    /**
     * 年级
     */
    private Long grade;
    /**
     * 课程数
     */
    private Integer curriculumCount;

    /**
     * 账号状态
     * 【0 正常， 1 禁言, 2 锁定】
     */
    private Integer status;

    /**
     * 禁言或锁定开始时间
     */
    private Long startLockTime;

    /**
     * 禁言或锁定时间
     */
    private Long lockTime;

    /**
     * 教师职称
     */
    private String title;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getTopImgUrl() {
        return topImgUrl;
    }

    public void setTopImgUrl(String topImgUrl) {
        this.topImgUrl = topImgUrl;
    }

    public String getUserIntro() {
        return userIntro;
    }

    public void setUserIntro(String userIntro) {
        this.userIntro = userIntro;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Long followerCount) {
        this.followerCount = followerCount;
    }

    public Long getFansCount() {
        return fansCount;
    }

    public void setFansCount(Long fansCount) {
        this.fansCount = fansCount;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }

    public Integer getCurriculumCount() {
        return curriculumCount;
    }

    public void setCurriculumCount(Integer curriculumCount) {
        this.curriculumCount = curriculumCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getStartLockTime() {
        return startLockTime;
    }

    public void setStartLockTime(Long startLockTime) {
        this.startLockTime = startLockTime;
    }

    public Long getLockTime() {
        return lockTime;
    }

    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
