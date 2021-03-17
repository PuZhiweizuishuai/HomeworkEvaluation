package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2021-03-16 23:24:52
 */
@Data
@TableName("think")
public class ThinkEntity {
	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 正文
	 */
	private String content;

	/**
	 * 类型
	 */
	private Integer type;

	/**
	 * 图片或正文
	 */
	private String files;

	/**
	 * 转发ID
	 */
	private Long forward;

	/**
	 * 转发量
	 */
	private Long forwardCount;

	/**
	 * 设备
	 */
	private String ua;

	/**
	 * ip
	 */
	private String ip;

	/**
	 * 
	 */
	private Long createTime;

	/**
	 * 
	 */
	private Long updateTime;

	/**
	 * 
	 */
	private Long likeCount;

	/**
	 * 
	 */
	private Long collectCount;

	/**
	 * 
	 */
	private Integer status;

	/**
	 * 
	 */
	private Long viewCount;

	/**
	 * 
	 */
	private String authorId;


	public void initData() {
		this.forwardCount = 0L;
		long time = System.currentTimeMillis();
		this.createTime = time;
		this.updateTime = time;
		this.likeCount = 0L;
		this.collectCount = 0L;
		this.viewCount = 0L;
		this.status = 0;
	}

}
