package com.buguagaoshu.homework.evaluation.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-05 21:43
 */
@Data
public class VerifyCodeVo {
    @NotBlank
    private String verifyCode;
}
