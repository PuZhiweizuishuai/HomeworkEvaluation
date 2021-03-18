package com.buguagaoshu.homework.click.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 收藏表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2021-03-17 23:32:57
 */
@Data
@TableName("collects")
public class CollectsEntity {
	/**
	 * 
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 收藏的帖子
	 */
	private Long articleId;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 收藏时间
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
	 * 收藏的细节，原帖标题或者内容
	 */
	private String text;

	/**
	 * 类型课程内帖子，论坛帖子，想法收藏
	 */
	private Integer type;

}
