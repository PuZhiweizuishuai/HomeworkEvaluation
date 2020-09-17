package com.buguagaoshu.homework.evaluation.model;

import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-06 14:13
 */
@Data
public class User implements UserDetails {
    /**
     * 主键ID，学号
     */
    private String userId;

    private String password;

    /**
     * 姓名
     */
    private String username;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机
     */
    private String phoneNumber;
    /**
     * 【0 男，1 女】
     */
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


    private UserRoleEntity role;

    private List<GrantedAuthority> authorities;

    private Long expirationTime;

    private String verifyCode;

    private Boolean rememberMe;

    /**
     * 邀请码
     * */
    private String invitationCode;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    /**
     * TODO 账号被锁定后的特殊处理
     * */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
