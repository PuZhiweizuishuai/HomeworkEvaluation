package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-05 23:37
 */
public enum PasswordStatusEnum {
    /**
     * 密码状态
     * */
    RESET_PASSWORD(1, "密码被重置"),
    NO_RESET_PASSWORD(0, "密码没有被重置")
    ;
    int code;
    String msg;

    PasswordStatusEnum(int code, String msg) {
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
