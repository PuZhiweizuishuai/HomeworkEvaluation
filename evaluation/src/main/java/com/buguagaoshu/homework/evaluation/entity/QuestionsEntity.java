package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.*;

import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * 问题表，需要与作业表关联
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Data
@TableName("questions")
public class QuestionsEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    /**
     * 题目
     */
    private String question;

    /**
     * 类型 【0 单选  1 多选  2 填空  3 问答】
     */
    private Integer type;

    /**
     * 答案，保存为json对象，方便判断
     * 选择题答案
     */
    private String answer;

    /**
     * 问答题，填空题答案
     * */
    private String otherAnswer;

    private String options;

    /**
     * 提示
     */
    private String tips;
    /**
     * 是否分享 【0 私有  1 其它老师可见 2 所有人可见】
     */
    private Integer shareStatus;

    /**
     * 创建老师
     */
    private String createTeacher;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 提交次数
     */
    private Long submitCount;

    /**
     * 正确次数
     */
    private Long rightCount;


    /**
     * 难度 0 ---- 10
     * */
    private Integer difficulty;


    /**
     * 标签
     * */
    private String tag;



    @TableField(exist = false)
    private Integer score;

    @Version
    private Integer version;

}
