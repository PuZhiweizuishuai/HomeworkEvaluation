package com.buguagaoshu.homework.evaluation.model;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-14 17:47
 */
public class QuestionsModel {
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
     * 选择题选项
     * */
    private List<String> options;

    /**
     * 选择题答案，保存为json对象，方便判断
     */
    private List<String> answer;

    /**
     * 判断，问答，填空题答案
     * */
    private String otherAnswer;

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
     * 题目分值
     * */
    private Integer score;
}
