package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 作业表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Data
@TableName("homework")
public class HomeworkEntity {
    /**
     * 作业ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 作业标题
     */
    private String title;
    /**
     * 作业要求
     */
    private String content;

    /**
     * 附件url，使用英文逗号分割
     */
    private String fileUrl;
    /**
     * 开始时间
     */
    private Long openTime;
    /**
     * 截止时间
     */
    private Long closeTime;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 创建老师
     */
    private String createTeacher;
    /**
     * 所属班级
     */
    private Long classNumber;
    /**
     * 当前提交人数
     */
    private Integer submitCount;

    /**
     * 作业类型， 【0 普通作业，在时间区间完成即可， 1 测验考试，进入后需要在限时时间内完成】
     */
    private Integer type;


    /**
     * 如果时测验，如果时测验，测验开始几分钟后不能进入
     */
    private Integer limitTime;

    /**
     * 测验时间
     */
    private Integer time;


    private Integer status;

    private Integer score;

    private Integer sourceType;
}
