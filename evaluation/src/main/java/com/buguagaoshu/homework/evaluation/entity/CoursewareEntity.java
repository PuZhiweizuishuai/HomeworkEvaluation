package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import com.buguagaoshu.homework.common.valid.ListValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-26 21:37:00
 */
@Data
@TableName("courseware")
public class CoursewareEntity {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 课件标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 详细介绍
     */
    private String text;

    /**
     * 上传的课件
     */
    private String fileUrl;

    /**
     * 课件类型，控制预览与下载
     */
    private Integer fileType;

    /**
     * 创建时间
     */
    private Long caretTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 分级【0 父分级， 1 子分级】
     */
    @ListValue(value = {0, 1, 2})
    private Integer level;

    /**
     * 创建老师
     */
    private String createTeacher;

    /**
     * 父分级
     */
    private Long fatherId;


    /**
     * 排序，如第一章，第二章
     */
    private Integer sort;

    /**
     * 原文件名
     * */
    private String fileName;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    private List<CoursewareEntity> children;


    @TableField(exist = false)
    private String key;
}
