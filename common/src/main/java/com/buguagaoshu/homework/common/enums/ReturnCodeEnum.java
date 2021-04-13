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

    SYSTEM_ERROR(500, "系统异常，请重试"),

    USER_ALREADY_EXISTS(1000, "用户已经存在,请直接尝试登录！"),
    USER_NOT_FIND(1001, "用户不存在！"),
    USER_ID_BAD(1002, "输入学号或ID不符合要求"),
    USER_ROLE_BAD(1003, "权限设置错误！"),
    USER_LOCK_TYPE_BAD(1004, "账号锁定或禁言类型错误"),
    NOT_LOGGED_IN(1005, "未登陆"),
    NO_POWER(1006, "没有权限"),


    CANNOT_BE_ALTER_YOUR_ROLE(2000, "不能自己修改自己的角色"),
    NO_ALTER_ROLE_POWER(2001, "没有修改角色的权限！"),
    ROLE_TYPE_ERROR(2002, "角色类型设置错误!"),
    NO_ROLE_OR_NO_FOUND(2003, "没有找到或没有权限"),

    NOT_SELECT_THIS_COURSE(3000, "未加入这门课程"),
    CANNOT_SUBMIT_HOMEWORK(3001, "作业已经提交，不能重复提交！" ),
    NOT_READ_QUESTION(3002, "请先阅读题目后再提交作业"),
    MISS_SUBMIT_TIME(3003, "你已经错过了提交时间，作业已经无法提交。"),
    ALREADY_JOINED(3004, "已经加入这门课程，无需重复加入！"),
    NO_TIME(3005, "未到开课时间！"),
    COURSE_IS_CLOSE(3006, "课程已经结束，无法加入！"),
    EXCEED_JOIN_LIMIT_TIME(3007, "超过课程最晚加入时间。"),

    LACK_ID(4000, "缺少ID"),
    NOT_RUN(4001, "没有在运行"),
    DATA_VALID_EXCEPTION(4002, "数据校验错误"),
    NO_SCORE_DATA(4003, "缺少成绩数据或成绩数据不合规定，请检查后重试！")
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
