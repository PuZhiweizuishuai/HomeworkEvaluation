package com.buguagaoshu.homework.click.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户关注表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2021-03-17 23:32:57
 */
@Data
@TableName("follow_user")
public class FollowUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	@TableId
	private Long id;

	/**
	 * 被关注对象ID
	 */
	private String targetUserId;

	/**
	 * 发起关注的用户
	 */
	private String userId;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 
	 */
	private String ip;

	/**
	 * 
	 */
	private String ua;

	/**
	 * 关注分组
	 */
	private String group;

	/**
	 * 关注0 公开， 1 私密关注
	 */
	private Integer type;

}
