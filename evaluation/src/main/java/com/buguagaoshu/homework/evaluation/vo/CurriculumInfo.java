package com.buguagaoshu.homework.evaluation.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-11 18:23
 * 课程信息页的课程信息数据
 */
@Data
public class CurriculumInfo {
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
     * 老师头像
     * */
    private String teacherAvatar;

    /**
     * 老师所在学校
     * */
    private String teacherSchool;

    /**
     * 课程名
     */
    private String curriculumName;
    /**
     * 班级号 如 1 班
     */
    private Integer classNumber;
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

    /**
     * 【0 邀请进入】【1 输入密码进入】【3 公开】
     */
    private Integer accessMethod;


    /**
     * 【0 不限制加入时间，结课前均可加入， 1 限制加入时间】
     * */
    private Integer joinTimeLimit;


    /**
     * 限制加入时间
     * 超过加入时间后只能邀请进入，或者延长时间
     * */
    private Long joinTime;


    private Long updateTime;


    private String courseTagName;

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

    private String title;


    /**
     * 本课程的其他老师
     * */
    private List<SimpleTeacherInfo> otherTeacher;
}
