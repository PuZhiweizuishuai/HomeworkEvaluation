package com.buguagaoshu.homework.click.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 点赞或点踩表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2021-03-17 23:32:57
 */
@Data
@TableName("like_or_unlike")
public class LikeOrUnlikeEntity {
	/**
	 * 
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 帖子或评论
	 */
	private Long targetId;

	/**
	 * 通知的用户
	 */
	private String targetUserId;

	/**
	 * 发起点赞的用户
	 */
	private String receiverUser;

	/**
	 * 类型评论点赞还是帖子点赞，还是点了踩
	 */
	private Integer type;

	/**
	 * 
	 */
	private Long createTime;

	/**
	 * 0 未读，1已读
	 */
	private Integer status;

	/**
	 * 描述信息
	 */
	private String text;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	/**
	 * 缓存点赞人名称
	 */
	private String receiverUserName;

}
