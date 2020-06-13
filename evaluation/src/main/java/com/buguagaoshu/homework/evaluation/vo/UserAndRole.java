package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 20:12
 * 携带角色返回用户列表
 */
@Data
public class UserAndRole {
    private String userId;

    /**
     * 姓名
     */
    private String username;

    /**
     * 【0 男，1 女】
     */
    private Integer sex;

    /**
     * 头像地址
     */
    private String userAvatarUrl;

    /**
     * 用户简介
     */
    private String userIntro;
    /**
     * 加入时间
     */
    private String createTime;
    /**
     * 最近一次登陆时间
     */
    private String latestLoginTime;
    /**
     * 最近一次登陆ip
     */
    private String latestLoginIp;
    /**
     * 发帖计数
     */
    private Long articleCount;
    /**
     * 回帖计数
     */
    private Long commentCount;
    /**
     * 获赞计数
     */
    private Long likeCount;

    /**
     * 生日 yyyy-MM-dd
     * */
    private String birthday;

    /**
     * 关注者数量
     */
    private Long followerCount;
    /**
     * 粉丝数量
     */
    private Long fansCount;

    /**
     * 学校
     */
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
     * 课程数
     */
    private Integer curriculumCount;

    /**
     * 禁言或锁定开始时间
     * */
    private Long startLockTime;

    /**
     * 禁言或锁定时间
     * */
    private Long lockTime;

    /**
     * 账号状态
     * 【0 正常， 1 禁言, 2 锁定】
     * */
    private Integer status;


    private UserRoleEntity role;

    private String classRole;
}
