package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;


/**
 * 问题-作业关联表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Data
@TableName("homework_with_questions")
public class HomeworkWithQuestionsEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 作业ID
     */
    private Long homeworkId;
    /**
     * 问题ID
     */
    private Long questionId;

    /**
     * 分值
     */
	private Integer score;

    private String createTeacher;

    private Long createTime;
}
