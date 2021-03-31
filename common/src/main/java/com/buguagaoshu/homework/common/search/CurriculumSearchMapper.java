package com.buguagaoshu.homework.common.search;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 22:52
 */
public class CurriculumSearchMapper {
    private Long id;
    /**
     * 创建的老师
     */
    private String createTeacher;

    /**
     * 老师名
     * */
    private String teacherName;
    /**
     * 课程名
     */
    private String curriculumName;

    /**
     * 开课时间
     */
    private Long openingTime;
    /**
     * 结课时间
     */
    private Long closeTime;
    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 简短的介绍，显示在课程名底下
     * */
    private String simpleInfo;

    /**
     * 课程描述
     * 详细的介绍
     */
    private String curriculumInfo;
    /**
     * 课程图片
     */
    private String curriculumImageUrl;
    /**
     * 学生人数
     */
    private Integer studentNumber;


    private Long updateTime;

    /**
     * 父级分类
     * */
    private Long fatherCourseTag;

    /**
     * 课程所属分类
     * */
    private Long courseTag;

    /**
     * 评分，综合所有学生打分得出
     * 初始为0分，不显示，显示咱没有人评分
     * */
    private Double score;

    private Integer ratingUserNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateTeacher() {
        return createTeacher;
    }

    public void setCreateTeacher(String createTeacher) {
        this.createTeacher = createTeacher;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCurriculumName() {
        return curriculumName;
    }

    public void setCurriculumName(String curriculumName) {
        this.curriculumName = curriculumName;
    }

    public Long getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Long openingTime) {
        this.openingTime = openingTime;
    }

    public Long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getSimpleInfo() {
        return simpleInfo;
    }

    public void setSimpleInfo(String simpleInfo) {
        this.simpleInfo = simpleInfo;
    }

    public String getCurriculumInfo() {
        return curriculumInfo;
    }

    public void setCurriculumInfo(String curriculumInfo) {
        this.curriculumInfo = curriculumInfo;
    }

    public String getCurriculumImageUrl() {
        return curriculumImageUrl;
    }

    public void setCurriculumImageUrl(String curriculumImageUrl) {
        this.curriculumImageUrl = curriculumImageUrl;
    }

    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getFatherCourseTag() {
        return fatherCourseTag;
    }

    public void setFatherCourseTag(Long fatherCourseTag) {
        this.fatherCourseTag = fatherCourseTag;
    }

    public Long getCourseTag() {
        return courseTag;
    }

    public void setCourseTag(Long courseTag) {
        this.courseTag = courseTag;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getRatingUserNumber() {
        return ratingUserNumber;
    }

    public void setRatingUserNumber(Integer ratingUserNumber) {
        this.ratingUserNumber = ratingUserNumber;
    }
}
