package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-04 22:31
 */
public enum AtUserTypeEnum {
    /**
     * '@' 用户类型
     * */
    COMMENT_AT(0, "评论@"),

    ARTICLE_AT(1, "正文@");

    int code;

    String msg;

    AtUserTypeEnum(int code, String msg) {
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
