package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 12:43
 * 作业类型
 */
public enum HomeworkTypeEnum {
    /**
     * 作业类型
     * */
    ORDINARY_HOMEWORK(0, "普通作业，不限时间"),
    TEST_HOMEWORK(1, "限时测验，考试")
    ;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    int code;
    String msg;

    HomeworkTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
