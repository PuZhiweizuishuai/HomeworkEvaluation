package com.buguagaoshu.homework.evaluation.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-15 19:19
 * 用户投票选择
 */
@Data
public class UserVoteChoices {
    @NotNull(message = "投票ID不能为空！")
    private String id;


    @NotNull(message = "投票选项不能为空！")
    private List<String> choices;
}
