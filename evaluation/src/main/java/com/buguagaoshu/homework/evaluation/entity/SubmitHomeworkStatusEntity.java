package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 用户作业提交状态
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-16 18:47:06
 */
@Data
@TableName("submit_homework_status")
public class SubmitHomeworkStatusEntity {
	/**
	 *
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 作业Id
	 */
	private Long homeworkId;

	/**
	 * 状态
     HOMEWORK_ERROR(-1, "作业不符合要求，被打回。"),
    NOT_SUBMITTED(0, "暂未提交"),
    TEMPORARY_STORAGE(1, "暂时保存，但未提交"),
    SUBMIT(2, "已经提交，但老师没有批改"),
    CORRECTION(3, "老师批改完成")
	 */
	private Integer status;

	/**
	 * 得分
	 * */
	private Double score;

	/**
	 * 创建时间
	 * 第一次进入的时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	/**
	 * 教师评价
	 * */
	private String teacherComment;
}
