package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 用户提交的答案保存
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-16 18:47:06
 */
@Data
@TableName("submit_questions")
public class SubmitQuestionsEntity {
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 选择答案
	 */
	private String answer;

	/**
	 * 其它答案
	 */
	private String otherAnswer;

	/**
	 * 问题ID
	 */
	private Long questionId;

	/**
	 * 作业ID
	 */
	private Long homeworkId;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	private Double score;


	private Integer type;


	private Integer maxScore;

	/**
	 * 教师点评
	 * */
	private String teacherComment;
}
