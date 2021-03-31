package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * 通知表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-26 21:36:59
 */
@Data
@TableName("notification")
public class NotificationEntity {
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    /**
     * 通知发送人ID
     */
    private String notifier;

    /**
     * 通知接收人ID
     */
    private String receiver;

    /**
     * 外部ID，如主帖子ID,课程ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long outerId;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论目标ID
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long commentId;

    /**
     * 类型 ，具体内容见枚举类
     */
    private Integer type;

    /**
     *
     */
    private Long createTime;

    /**
     * 【0 未读， 1 已读】
     */
    private Integer status;

    /**
     * 简单的信息描述
     */
    private String text;

    /**
     * 跳转链接
     * */
    private String url;


    private String notifierName;
}
