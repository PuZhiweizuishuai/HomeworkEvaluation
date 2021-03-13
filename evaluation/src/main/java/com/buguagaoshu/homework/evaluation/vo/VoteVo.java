package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.common.valid.ListValue;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import java.util.List;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-13 22:57
 */
@Data
public class VoteVo {
    private Long id;

    @Max(value = 50, message = "标题过长，最大不超过50个字！")
    private String title;

    @NotNull(message = "选项不能为空！")
    private List<String> choices;

    private Integer maxChoice;

    private String endTime;

    @ListValue(value = {0, 1}, message = "类型取值错误")
    private Integer type;

    private Long createTime;

    private Long voteCount;
}
