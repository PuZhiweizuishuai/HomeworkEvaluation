package com.buguagaoshu.homework.evaluation.vo;

import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-30 22:34
 */
@Data
public class UserUpdateVo {
    private String topImgUrl;

    /**
     * 头像地址
     */
    private String userAvatarUrl;


    private String school;
    /**
     * 专业
     */
    private String major;
    /**
     * 年级
     */
    private Long grade;

    /**
     * qq
     */
    private String userQq;
    /**
     * 微信
     */
    private String userWechat;
    /**
     * 用户简介
     */
    private String userIntro;
}
