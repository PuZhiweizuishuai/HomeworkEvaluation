package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import lombok.Data;

/**
 * 负责关联每次作业中每道题目的分值
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-14 20:42:02
 */
@Data
@TableName("questions_score_in_homework")
public class QuestionsScoreInHomeworkEntity {
	/**
	 *
	 */
	@TableId
	private Long id;

	/**
	 * 分数
	 */
	private Integer score;

	/**
	 * 关联的问题
	 */
	private Long questionId;

	/**
	 * 作业ID
	 */
	private Long homewokId;

	/**
	 * 时间
	 */
	private Long createTime;

}
