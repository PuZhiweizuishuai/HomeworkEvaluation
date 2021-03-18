package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.buguagaoshu.homework.common.enums.InviteCodeTypeEnum;
import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.utils.InviteCodeUtil;
import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 邀请码
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Data
@TableName("invite_code")
public class InviteCodeEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;
    /**
     * 邀请码
     */
    private String code;
    /**
     * 生成者ID
     */
    private String generatorId;
    /**
     * 过期时间
     */
    private Long expireTime;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 使用次数，即当前邀请码可使用次数，默认为 1
     */
    @Min(value = 1, message = "使用次数最小为1")
    private Long useCount;
    /**
     * 生成的邀请链接
     */
    private String linkUrl;
    /**
     * 状态  0：可使用，1 停用
     */
    @ListValue(value = {0, 1}, message = "邀请码状态设置错误！")
    private Integer status;
    /**
     * 描述
     */
    private String memo;

    /**
     * 邀请码类型 【0 课程邀请码】【1 用户邀请码】
     * */
    @NotNull(message = "类型设置不能为空")
    @ListValue(value = {0,1}, message = "邀请码类型设置错误！")
    private Integer type;

    /**
     * 对应的班级
     */
    private Long classNumber;

    /**
     * 角色
     * */
    private String role;


    public void initData(String userId) {
        this.code = InviteCodeUtil.createInviteCode();
        this.createTime = System.currentTimeMillis();
        this.generatorId = userId;
        this.status = InviteCodeTypeEnum.AVAILABLE.getCode();
    }
}
