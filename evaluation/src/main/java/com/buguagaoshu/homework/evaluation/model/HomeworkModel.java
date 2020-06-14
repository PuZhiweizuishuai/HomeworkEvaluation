package com.buguagaoshu.homework.evaluation.model;


import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-14 17:47
 */
public class HomeworkModel {
    /**
     * 作业标题
     */
    private String title;
    /**
     * 作业要求
     */
    private String content;

    /**
     * 附件url，使用英文逗号分割
     */
    private String fileUrl;
    /**
     * 开始时间
     */
    private Long openTime;
    /**
     * 截止时间
     */
    private Long closeTime;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 创建老师
     */
    private String createTeacher;
    /**
     * 所属班级
     */
    private Long classNumber;
    /**
     * 当前提交人数
     */
    private Integer submitCount;

    /**
     * 作业类型， 【0 普通作业，在时间区间完成即可， 1 测验考试，进入后需要在限时时间内完成】
     */
    private Integer type;


    /**
     * 如果时测验，如果时测验，测验开始几分钟后不能进入
     */
    private Integer limitTime;

    /**
     * 测验时间
     */
    private Integer time;


    private Integer status;

    /**
     * 问题列表
     * */
    List<QuestionsModel> questionsModels;
}
