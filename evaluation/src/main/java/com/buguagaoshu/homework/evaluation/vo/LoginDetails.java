package com.buguagaoshu.homework.evaluation.vo;

import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-06 14:05
 */
@Data
public class LoginDetails {
    private Boolean rememberMe;
    private String verifyCode;

    public LoginDetails(Boolean rememberMe, String verifyCode) {
        this.rememberMe = rememberMe;
        this.verifyCode = verifyCode;
    }

    public LoginDetails() {
    }
}
