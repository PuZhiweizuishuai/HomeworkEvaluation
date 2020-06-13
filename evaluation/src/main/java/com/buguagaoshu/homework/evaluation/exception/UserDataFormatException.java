package com.buguagaoshu.homework.evaluation.exception;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-12 21:50
 * 用户提交数据不符合规定异常
 */
public class UserDataFormatException extends RuntimeException {
    public UserDataFormatException() {
    }

    public UserDataFormatException(String message) {
        super(message);
    }

    public UserDataFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDataFormatException(Throwable cause) {
        super(cause);
    }

    public UserDataFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
