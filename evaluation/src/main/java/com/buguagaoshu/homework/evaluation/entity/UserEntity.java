package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.valid.OnlyNumber;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

/**
 * 用户表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Data
@TableName("user")
public class UserEntity {
    /**
     * 主键ID，学号
     */
    @TableId
    @OnlyNumber(message = "学号必须全部是数字，并且长度小于20！")
    private String userId;
    /**
     * 密码
     */
    private String password;
    /**
     * 姓名
     */
    @Length(max = 20, message = "姓名最长不超过20个字")
    private String username;
    /**
     * 邮箱
     */
    @Email(message = "邮件格式不对")
    private String email;

    /**
     * 手机
     */
    private String phoneNumber;

    /**
     * 【0 男，1 女】
     */
    @ListValue(value = {0, 1}, message = "性别设置不对")
    private Integer sex;

    /**
     * 生日 yyyy-MM-dd
     * */
    private String birthday;
    /**
     * 头像地址
     */
    private String userAvatarUrl;

    /**
     * 个人主页顶部大图
     * */
    private String topImgUrl;

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
    /**
     * 加入时间
     */
    private Long createTime;
    /**
     * 最近一次登陆时间
     */
    private Long latestLoginTime;
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
     * 关注者数量
     */
    private Long followerCount;
    /**
     * 粉丝数量
     */
    private Long fansCount;
    /**
     * 是否公开qq【1 公开  0 不公开】
     */
    private Integer userQqStatus;
    /**
     * 是否公开微信 【1 公开  0 不公开】
     */
    private Integer userWechatStatus;
    /**
     * 是否公开邮箱 【1 公开  0 不公开】
     */
    private Integer userEmailStatus;
    /**
     * 是否公开手机 【1 公开  0 不公开】
     */
    private Integer userPhoneStatus;
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
     * 账号状态
     * 【0 正常， 1 禁言, 2 锁定】
     * */
    private Integer status;

    /**
     * 禁言或锁定开始时间
     * */
    private Long startLockTime;

    /**
     * 禁言或锁定时间
     * */
    private Long lockTime;

    /**
     * 是否是首次登陆
     * 0 是
     * 1 不是
     * 首次登陆需要补全信息
     * */
    private Integer firstLoginStatus;


    /**
     * 是否被重置密码
     * 0 不是
     * 1 是
     * 被重置密码后需要弹出修改密码窗口
     * */
    private Integer resetPasswordStatus;


    /**
     * 教师职称
     * */
    private String title;

    @TableField(exist = false)
    private String role;

    @TableField(exist = false)
    private UserRoleEntity roleEntity;

}
