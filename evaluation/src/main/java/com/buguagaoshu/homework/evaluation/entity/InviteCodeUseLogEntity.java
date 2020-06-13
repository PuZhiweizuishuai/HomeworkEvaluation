package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import lombok.Data;

/**
 * 邀请码使用列表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Data
@TableName("invite_code_use_log")
public class InviteCodeUseLogEntity {

	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 邀请码ID
	 */
	private Long invitecodeId;
	/**
	 * 使用者ID
	 */
	private String studentId;
	/**
	 * 使用时间
	 */
	private Long useTime;

}
