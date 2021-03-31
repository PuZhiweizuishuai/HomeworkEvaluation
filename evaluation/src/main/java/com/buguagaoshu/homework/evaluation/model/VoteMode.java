package com.buguagaoshu.homework.evaluation.model;

import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-15 18:59
 */
@Data
public class VoteMode {
    private String id;

    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long articleId;

    @Max(value = 50, message = "标题过长，最大不超过50个字！")
    private String title;

    @NotNull(message = "选项不能为空！")
    private Map<String, Long> choices;

    private Integer maxChoice;

    private Long endTime;

    @ListValue(value = {0, 1}, message = "类型取值错误")
    private Integer type;

    private Long createTime;

    private Long voteCount = 0L;
}
