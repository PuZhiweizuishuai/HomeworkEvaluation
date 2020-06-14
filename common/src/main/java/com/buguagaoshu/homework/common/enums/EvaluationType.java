package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-14 14:08
 */
public enum  EvaluationType {
    /**
     * 评价状态
     * */
    HOMEWORK_NO_PUBLISH(-1, "作业未发布，需要等待添加题目后发布！"),
    HOMEWORK_NO_START(0, "作业未开始"),
    EVALUATION_NO_START(1, "作业开始，评价未开始"),
    EVALUATION_START(2, "评价开始"),
    FINISH_EVALUATION(3, "评价完成")
    ;
    int code;
    String msg;

    EvaluationType(int code, String msg) {
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
