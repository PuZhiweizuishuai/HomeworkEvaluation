package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * 
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-28 20:50:50
 */
@Data
@TableName("article_tag")
public class ArticleTagEntity {
	@TableId(type = IdType.ASSIGN_ID)
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long id;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 图标
	 */
	private String icon;

	/**
	 * 简单描述
	 */
	private String descript;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 0 父分类， 其它数字为该数字下的子 子分类
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long catelogId;

	/**
	 * 0 话题， 1 分类
	 */
	private Integer type;

	private Long commentCount;

	private Long followCount;


	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)
	private List<ArticleTagEntity> children;

}
