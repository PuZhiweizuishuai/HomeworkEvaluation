package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.valid.OnlyNumber;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-02 19:24
 */
@Data
public class RegisterUserVo {
    @Email(message = "邮件格式错误")
    private String email;

    //@OnlyNumber(max = 11, message = "手机号格式错误")
    private String phoneNumber;


    @Length(min = 6, message = "密码必须在6位以上")
    private String password;

    private String invitationCode;


    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

    @NotBlank(message = "用户名不能为空！")
    private String username;


    @OnlyNumber(message = "学号必须全是数字")
    private String userId;


    @ListValue(value = {0, 1}, message = "性别填写错误！")
    private Integer sex;

    private String code;


    private String birthday;
}
