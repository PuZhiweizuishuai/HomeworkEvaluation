package com.buguagaoshu.homework.evaluation.vo;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-11 22:25
 */
public class SimpleCourseVo {
    /**
     * 创建的老师
     */
    private String createTeacher;

    private String teacherName;
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
     * 简短的介绍，显示在课程名底下
     * */
    private String simpleInfo;


    /**
     * 课程图片
     */
    private String curriculumImageUrl;
    /**
     * 学生人数
     */
    private Integer studentNumber;

}
