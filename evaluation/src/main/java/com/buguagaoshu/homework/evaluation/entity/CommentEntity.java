package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


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
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 帖子ID
	 */
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
	 * 0 一级回复， 1 回复评论
	 */
	private Integer type;

	/**
	 * 回复的帖子ID
	 */
	private Long commentId;

	/**
	 * 状态 0 正常， 1 删除
	 */
	private Integer status;

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

}
