package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import com.baomidou.mybatisplus.annotation.Version;
import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * 评论
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-28 20:13:56
 */
@Data
@TableName("comment")
public class CommentEntity {
	/**
	 * 回帖主键ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long id;

	/**
	 * 帖子ID
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long articleId;

	/**
	 * 回复正文
	 */
	private String content;

	/**
	 * 作者ID
	 */
	private String authorId;

	/**
	 * '@'用户列表
	 * 写入数据库为JSON
	 * 接收 List<AtUser> 数据类型
	 * */
	private String atUser;

	/**
	 * 0 一级回复， 1 回复评论
	 */
	private Integer type;

	/**
	 * 父级评论ID
	 * 如果没有，默认为帖子ID
	 * */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long fatherId;

	/**
	 * 回复的帖子ID
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long commentId;

	/**
	 * 状态 0 正常， 1 删除
	 */
	private Integer status;

	private Long commentCount;

	/**
	 * IP地址
	 */
	private String ip;

	/**
	 * 回复设备UA
	 */
	private String ua;

	/**
	 * 喜欢数
	 */
	private Long likeCount;

	/**
	 * 不喜欢数
	 */
	private Long badCount;

	/**
	 * 回复是否被采纳，用于问答贴，0 未采纳， 1被采纳
	 */
	private Integer qAOffered;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 修改时间
	 */
	private Long updateTime;


	private Double rating;

	@Version
	private Integer version;


	public void initComment() {
		long time = System.currentTimeMillis();
		this.status = 0;
		this.likeCount = 0L;
		this.badCount = 0L;
		this.qAOffered = 0;
		this.createTime = time;
		this.updateTime = time;
		this.commentCount = 0L;
	}

}
