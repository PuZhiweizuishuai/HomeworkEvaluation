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
 * 用户作业提交状态
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-16 18:47:06
 */
@Data
@TableName("submit_homework_status")
public class SubmitHomeworkStatusEntity {
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 学生姓名的冗余存储
     */
    private String studentName;

    /**
     * 作业Id
     */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long homeworkId;

    /**
     * 状态
     * HOMEWORK_ERROR(-1, "作业不符合要求，被打回。"),
     * NOT_SUBMITTED(0, "暂未提交"),
     * TEMPORARY_STORAGE(1, "暂时保存，但未提交"),
     * SUBMIT(2, "已经提交，但老师没有批改"),
     * CORRECTION(3, "老师批改完成")
     */
    private Integer status;

    /**
     * 得分
     */
    private Double score;

    /**
     * 创建时间
     * 第一次进入的时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 提交时间
     */
    private Long submitTime;

    /**
     * 教师评价
     */
    private String teacherComment;

    /**
     * 评价人数
     * */
    private Integer commentCount;

    /**
     * 喜欢数
     * */
    private Integer likeCount;


    /**
     * 评价等级
     * */
    private Double rating;

    @Version
    private Integer version;
}
