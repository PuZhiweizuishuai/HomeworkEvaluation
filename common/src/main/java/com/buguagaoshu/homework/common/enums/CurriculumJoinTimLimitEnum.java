package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-07 13:42
 */
public enum CurriculumJoinTimLimitEnum {
    /**
     * 是否限制加入时间
     * */
    NO_JOIN_TIME(0, "不限制加入时间，结课前可加入"),
    LIMIT_JOIN_TIME(1, "限制加入时间")
    ;
    int code;
    String string;

    CurriculumJoinTimLimitEnum(int code, String string) {
        this.code = code;
        this.string = string;
    }

    public int getCode() {
        return code;
    }

    public String getString() {
        return string;
    }
}
