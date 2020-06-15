package com.buguagaoshu.homework.evaluation.model;


import com.buguagaoshu.homework.common.valid.ListValue;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-14 17:47
 */
@Data
public class HomeworkModel {
    private Long id;
    /**
     * 作业标题
     */
    @NotEmpty(message = "作业标题不能为空")
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
    @NotNull(message = "作业开始时间不能为空")
    @Min(value = 0,message = "开始时间不能为空")
    private Long openTime;
    /**
     * 截止时间
     */
    @NotNull(message = "作业结束时间不能为空")
    @Min(value = 0,message = "结束时间不能为空")
    private Long closeTime;


    /**
     * 所属班级
     */
    @NotNull(message = "班级号不能为空")
    @Min(value = 0, message = "班级号错误")
    private Long classNumber;


    /**
     * 作业类型， 【0 普通作业，在时间区间完成即可， 1 测验考试，进入后需要在限时时间内完成】
     */
    @ListValue(value = {0, 1, 2}, message = "提交的类型错误")
    private Integer type;


    /**
     * 如果时考试，考试开始几分钟后不能进入
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
    @NotEmpty(message = "问题不能为空")
    List<QuestionsModel> questionsModels;
}
