package com.buguagaoshu.homework.evaluation.model;

import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-19 14:23
 * 加入课程需要使用的邀请码或密码
 */
@Data
public class JoinCourseCode {
    private String verifyCode;

    private String code;
}
