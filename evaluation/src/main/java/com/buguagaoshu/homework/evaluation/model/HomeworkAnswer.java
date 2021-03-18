package com.buguagaoshu.homework.evaluation.model;

import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-16 18:23
 */
@Data
public class HomeworkAnswer {
    /**
     * 作业ID
     * */
    @NotNull(message = "提交的作业数据有误")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long homeworkId;

    @ListValue(value = {0, 1, 2}, message = "提交格式错误")
    private Integer type;

    /**
     * 答案列表
     * */
    @NotEmpty(message = "作业列表没有答案")
    private List<UserSubmitQuestion> answers;
}
