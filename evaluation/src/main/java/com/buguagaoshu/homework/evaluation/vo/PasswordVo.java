package com.buguagaoshu.homework.evaluation.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-30 21:59
 */
@Data
public class PasswordVo {
    @NotBlank(message = "请输入原始密码")
    private String oldPassword;

    @NotBlank(message = "请输入新密码")
    @Length(min = 5, max = 25, message = "密码必须是6位以上")
    private String newPassword;


    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
