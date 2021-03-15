package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-28 21:43
 */
public enum ArticleTypeEnum {
    /**
     * 帖子类型
     * */
    DRAFT(-1, "草稿"),

    COURSE(0, "课程内的讨论贴！"),
    COURSE_QA(1, "课程内问答！"),
    COURSE_VOTE(2, "课程内投票！"),

    COURSE_END(9, "结束代码"),


    ORDINARY(10, "普通贴"),
    QA(11, "问答贴"),
    VOTE(12, "投票贴"),
    ORDINARY_END(99, "社区贴分隔符!"),

    THINK(100, "想法"),
    COURSE_RATING(200, "课程评分"),

    NORMAL(0, "帖子正常"),
    LOCK(1, "帖子已锁定"),
    DELETE(2, "帖子已删除"),
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


    /**
     * 判断是否是课程内帖子
     * */
    public static boolean checkCourseArticle(int code) {
        return code >= ArticleTypeEnum.COURSE.code && code <= ArticleTypeEnum.COURSE_END.code;
    }


    public static boolean isVote(int code) {
        return code == ArticleTypeEnum.VOTE.code || code == ArticleTypeEnum.COURSE_VOTE.code;
    }
}
