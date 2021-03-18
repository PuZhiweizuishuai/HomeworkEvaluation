package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * 角色表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Data
@TableName("user_role")
public class UserRoleEntity {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;
    /**
     * 学号
     */
    private String userId;
    /**
     * ROLE_加权限 如
     * 【 ROLE_ADMIN ： 管理员】
     * 【ROLE_TEACHER : 老师】
     * 【ROLE_ASSISTANT ： 助教】
     * 【ROLE_STUDENT : 学生】
     */
    private String role;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 创建人，默认初始状态为system
     */
    private String operator;

    /**
     * @deprecated 弃用，助教权限已经在班级与学生表后进行关联
     *
     * 助教权限所在的班级
     * 只在role值为ROLE_ASSISTANT时使用
     * 一串数字，中间用英文逗号分隔
     */
    private String classNumber;

}
