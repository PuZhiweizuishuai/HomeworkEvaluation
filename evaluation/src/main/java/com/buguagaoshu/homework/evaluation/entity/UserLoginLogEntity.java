package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 用户登陆记录
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-04 12:28:16
 */
@Data
@TableName("user_login_log")
public class UserLoginLogEntity {
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
	 * 登陆时间
	 */
	private Long loginTime;

	/**
	 * 登陆IP
	 */
	private String loginIp;

	/**
	 * User-Agent 浏览器信息
	 */
	private String loginUa;

	/**
	 * 登陆城市
	 */
	private String loginCity;

}
