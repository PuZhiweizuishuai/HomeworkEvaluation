package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 11:58
 * 作业提交状态
 */
public enum  HomeworkSubmitStatusEnum {
    /**
     * 作业提交后处理状态
     * */
    HOMEWORK_ERROR(-1, "作业不符合要求，被打回。"),
    TEMPORARY_STORAGE(0, "暂时保存，但未提交"),
    SUBMIT(1, "已经提交，但老师没有批改"),
    CORRECTION(2, "老师批改完成")
    ;

    int code;

    String msg;

    HomeworkSubmitStatusEnum(int code, String msg) {
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
     * 判断是否还能提交
     *
     * */
    public static boolean isSubmit(int code) {
        return code == -1 || code == 0;
    }
}
