package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-05 23:40
 */
public enum  LoginStatusEnum {
    /**
     * 登陆状态
     * */
    FIRST_LOGIN(0, "首次登陆"),
    NOT_FIRST_LOGIN(1, "非首次登陆");

    int code;
    String msg;

    LoginStatusEnum(int code, String msg) {
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
