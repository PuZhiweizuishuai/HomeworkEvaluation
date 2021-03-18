package com.buguagaoshu.homework.evaluation.model;

import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-16 18:22
 * 用户提交的作业答案
 */
@Data
public class UserSubmitQuestion {
    @NotNull(message = "问题ID不能为空")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long questionId;


    private String otherAnswer;

    private List<String> answer;
}
