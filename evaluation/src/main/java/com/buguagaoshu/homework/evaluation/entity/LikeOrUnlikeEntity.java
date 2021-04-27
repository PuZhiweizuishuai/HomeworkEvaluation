package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonSerializer;
import com.buguagaoshu.homework.common.valid.ListValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotNull;

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
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long id;

	/**
	 * 帖子或评论
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	@NotNull
	private Long targetId;

	/**
	 * 0 帖子， 1 评论
	 * */
	@ListValue(value = {0, 1}, message = "点赞类型只能为1或0")
	private Integer targetType;

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
	@ListValue(value = {0, 1})
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
