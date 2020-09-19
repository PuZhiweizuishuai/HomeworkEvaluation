package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 课程
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Data
@TableName("curriculum")
public class CurriculumEntity {
    /**
     * 课程 ID
     */
    @TableId(type = IdType.AUTO)
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
     * 【0 邀请进入】【1 输入密码进入】【2 公开】
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
    private Long joinTime;


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
}
