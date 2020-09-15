package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 11:55
 * 问题类型
 */
public enum QuestionTypeEnum {
    /**
     * 问题类型
     * */
    SINGLE_CHOICE(0, "单选"),
    MULTIPLE_CHOICE(1, "多选"),
    FILL_IN_THE_BLANKS(2, "填空"),
    QUESTIONS_ANSWERS(3, "问答，客观题"),
    JUDGE(4, "判断"),
    JUDGE_RIGHT(1, "判断题是对的"),
    JUDGE_ERROR(0, "判断题是错的"),
    SHARE_QUESTION(1, "分享这道问题"),
    NO_SHARE(0, "私有问题")
    ;
    int code;
    String msg;

    QuestionTypeEnum(int code, String msg) {
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
     * 判断是不是选择题
     * */
    public static boolean isChoice(int code) {
        return code == 0 || code == 1;
    }

    /**
     * 不是选择题
     * */
    public static boolean noChoice(int code) {
        return FILL_IN_THE_BLANKS.getCode() == code || QUESTIONS_ANSWERS.getCode() == code;
    }
}
