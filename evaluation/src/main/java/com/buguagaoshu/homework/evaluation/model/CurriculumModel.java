package com.buguagaoshu.homework.evaluation.model;

import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-09 16:34
 */
@Data
public class CurriculumModel {
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;
    /**
     * 创建的老师
     */
    private String createTeacher;
    /**
     * 课程名
     */
    @NotBlank(message = "课程名不能为空")
    @Length(max = 50, min = 1, message = "课程名不能超过50个字符")
    private String curriculumName;
    /**
     * 班级号 如 1 班
     */
    @NotNull(message = "班级号不能为空")
    private Integer classNumber;
    /**
     * 开课时间
     */
    @NotBlank(message = "开课时间不能为空")
    private String openingTime;
    /**
     * 结课时间
     */
    @NotBlank(message = "结课时间不能为空")
    private String closeTime;
    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 简短的介绍，显示在课程名底下
     * */
    @NotBlank(message = "简介不能为空")
    @Length(max = 100, min = 1, message = "简介不能超过100个字符")
    private String simpleInfo;

    /**
     * 课程描述
     * 详细的介绍
     */
    @NotBlank(message = "课程描述不能为空")
    private String curriculumInfo;
    /**
     * 课程图片
     */
    @NotBlank(message = "课程封面图不能为空")
    private String curriculumImageUrl;
    /**
     * 学生人数
     */
    private Integer studentNumber;

    /**
     * 【0 邀请进入】【1 输入密码进入】【3 公开】
     */
    @ListValue(value = {0, 1, 2}, message = "进入课程方法输入错误")
    private Integer accessMethod;

    /**
     * 课程密码，需要密码进入时使用
     */
    private String password;

    /**
     * 【0 不限制加入时间，结课前均可加入， 1 限制加入时间】
     * */

    @ListValue(value = {0, 1}, message = "限制进入时间输入错误")
    private Integer joinTimeLimit;


    /**
     * 限制加入时间
     * 超过加入时间后只能邀请进入，或者延长时间
     * */
    private String joinTime;


    /**
     * 父级分类
     * */
    @NotNull(message = "父分类不能为空")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long fatherCourseTag;

    /**
     * 课程所属分类
     * */
    @NotNull(message = "课程所属分类不能为空")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long courseTag;


    /**
     * 验证码
     * */
    private String verifyCode;
}
