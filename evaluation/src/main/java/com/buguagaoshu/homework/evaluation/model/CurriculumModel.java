package com.buguagaoshu.homework.evaluation.model;

import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-09 16:34
 */
@Data
public class CurriculumModel {
    private Long id;
    /**
     * 创建的老师
     */
    private String createTeacher;
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
    private String openingTime;
    /**
     * 结课时间
     */
    private String closeTime;
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
     * 课程密码，需要密码进入时使用
     */
    private String password;

    /**
     * 【0 不限制加入时间，结课前均可加入， 1 限制加入时间】
     * */
    private Integer joinTimeLimit;


    /**
     * 限制加入时间
     * 超过加入时间后只能邀请进入，或者延长时间
     * */
    private String joinTime;


    /**
     * 父级分类
     * */
    private Long fatherCourseTag;

    /**
     * 课程所属分类
     * */
    private Long courseTag;
}
