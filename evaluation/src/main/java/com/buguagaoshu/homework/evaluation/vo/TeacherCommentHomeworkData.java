package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-15 19:39
 * 教师提交的作业评价数据
 */
@Data
public class TeacherCommentHomeworkData {
    /**
     * 作业ID
     */
    @NotNull(message = "作业ID不能为空")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    private String studentId;

    /**
     * 总评
     */
    private String comment;

    /**
     * 设置作业状态
     */
    @ListValue(value = {-1 , 3}, message = "状态值只能是批改完成或打回")
    private Integer status;

    /**
     * 问题列表
     */
    private List<QuestionComment> questionList;
}
