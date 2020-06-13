package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 23:26
 */
public enum UserStatusEnum {
    /**
     * 账号状态
     * */
    NORMAL(0, "正常"),
    ESTOPPEL(1, "禁言中"),
    LOCKING(2, "账号被锁定，无法登陆")
    ;
    int code;
    String msg;

    UserStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static boolean check(int status) {
        for (UserStatusEnum userStatusEnum : UserStatusEnum.values()) {
            if (status == userStatusEnum.getCode()) {
                return true;
            }
        }
        return false;
    }
}
