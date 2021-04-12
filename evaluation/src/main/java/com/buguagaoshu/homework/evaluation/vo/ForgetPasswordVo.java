package com.buguagaoshu.homework.evaluation.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-12 21:00
 */
@Data
public class ForgetPasswordVo {
    @Email(message = "邮箱格式错误")
    private String email;

    private String verifyCode;

    private String code;

    @Length(max = 25, min = 6, message = "密码长度错误！")
    private String newPassword;

    @Length(max = 25, min = 6, message = "密码长度错误！")
    private String password;
}
