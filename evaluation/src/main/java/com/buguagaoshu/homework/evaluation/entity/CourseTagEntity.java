package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 课程分类
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-08 18:44:35
 */
@Data
@TableName("course_tag")
public class CourseTagEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程专业
     */
    @NotBlank(message = "分类名不能为空！")
    private String courseMajor;

    /**
     * 排序，数字越大越靠前
     */
    private Integer sort;

    /**
     * 描述
     */
    private String descript;

    /**
     * 图标
     */
    private String icon;

    /**
     * 所属分类层级 【0 父分类， 其它数字为该数字下的子 子分类】
     */
    private Long catelogId;


    @TableField(exist = false)
    private List<CourseTagEntity> children;
}
