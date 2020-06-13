package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 13:04
 * 系统返回错误代码
 */
public enum ReturnCodeEnum {
    /**
     * 系统错误吗
     * */
    SUCCESS(200, "Success!"),

    NOO_FOUND(404, "没有找到"),

    USER_ALREADY_EXISTS(1000, "用户已经存在！"),
    USER_NOT_FIND(1001, "用户不存在！"),
    USER_ID_BAD(1002, "输入学号或ID不符合要求"),
    USER_ROLE_BAD(1003, "权限设置错误！"),
    USER_LOCK_TYPE_BAD(1004, "账号锁定或禁言类型错误"),
    NOT_LOGGED_IN(1005, "未登陆"),


    CANNOT_BE_ALTER_YOUR_ROLE(2000, "不能自己修改自己的角色"),

    NOT_SELECT_THIS_COURSE(3000, "未加入这门课程"),

    LACK_ID(4000, "缺少ID"),
    NOT_RUN(4001, "没有在运行")
    ;
    int code;
    String msg;

    ReturnCodeEnum(int code, String msg) {
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
