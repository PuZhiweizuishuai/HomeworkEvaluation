package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-01 21:43
 */
public enum CommentTypeEnum {
    /**
     * 评论类型
     * */
    ORDINARY_ONE_LEVEL_COMMENT(0, "普通一级评论"),
    ORDINARY_SECOND_COMMENT(1, "普通二级评论"),

    HOMEWORK_ONE_LEVEL_COMMENT(10, "作业互评时的一级评论！"),
    HOMEWORK_SECOND_COMMENT(11, "作业互评二级评论！"),

    COURSE_ONE_LEVEL_COMMENT(100, "课程一级评价！"),
    COURSE_SECOND_COMMENT(101, "课程二级评论！")

    ;
    int code;
    String msg;

    CommentTypeEnum(int code, String msg) {
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
