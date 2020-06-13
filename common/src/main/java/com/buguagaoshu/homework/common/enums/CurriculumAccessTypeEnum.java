package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-07 13:29
 */
public enum CurriculumAccessTypeEnum {
    /**
     * 课程加入方法
     * */
    INVITATION(0, "邀请制"),
    USE_PASSWORD(1, "使用密码"),
    PUBLIC_CURRICULUM(2, "公开")
    ;
    int code;
    String msg;

    CurriculumAccessTypeEnum(int code, String msg) {
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
