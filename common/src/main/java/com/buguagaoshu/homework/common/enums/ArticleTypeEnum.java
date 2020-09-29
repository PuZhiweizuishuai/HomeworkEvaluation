package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-28 21:43
 */
public enum ArticleTypeEnum {
    /**
     * 帖子类型
     * */
    ORDINARY(0, "普通贴"),
    COURSE(1, "课程内的讨论贴"),
    QA(2, "问答贴"),
    THINK(3, "想法"),
    COURSE_RATING(4, "课程评分")
    ;

    int code;

    String msg;

    ArticleTypeEnum(int code, String msg) {
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
