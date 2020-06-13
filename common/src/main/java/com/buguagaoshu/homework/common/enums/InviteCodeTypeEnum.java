package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-07 13:51
 */
public enum InviteCodeTypeEnum {
    /**
     * 邀请码类型
     * */
    CURRICULUM(0, "课程邀请码"),
    USER(1, "用户邀请码")
    ;
    int code;
    String msg;

    InviteCodeTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
