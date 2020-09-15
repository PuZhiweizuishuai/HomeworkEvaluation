package com.buguagaoshu.homework.evaluation.model;

import com.buguagaoshu.homework.common.valid.ListValue;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-14 17:47
 */
@Data
public class QuestionsModel {
    private Long id;
    /**
     * 题目
     */
    @NotBlank(message = "题目内容不能为空")
    private String question;

    /**
     * 类型 【0 单选  1 多选  2 填空  3 问答】
     */
    @ListValue(value = {0,1,2,3}, message = "问题类型设置错误")
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
     * 做完题目后显示正确的答案
     * */
    private String rightAnswer;


    /**
     * 提示
     */
    private String tips;
    /**
     * 是否分享 【0 私有  1 其它老师可见 2 所有人可见】
     */
    @ListValue(value = {0, 1}, message = "分享类型设置错误")
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
     * 难度
     * */
    private Integer difficulty;


    /**
     * 标签
     * */
    private String tag;


    /**
     * 题目分值
     * */
    @Min(value = 1, message = "分数必须大于等于 1")
    private Integer score;

    /**
     * 实际得分
     * */
    private Double realityScore;


    /**
     * 教师点评
     * */
    private String teacherComment;
}
