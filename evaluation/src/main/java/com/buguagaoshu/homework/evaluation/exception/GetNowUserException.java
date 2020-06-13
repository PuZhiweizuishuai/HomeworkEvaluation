package com.buguagaoshu.homework.evaluation.exception;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-06 15:53
 * 获取当前用户状态异常
 *
 */
public class GetNowUserException extends RuntimeException {
    public GetNowUserException() {
        super();
    }

    public GetNowUserException(String message) {
        super(message);
    }

    public GetNowUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetNowUserException(Throwable cause) {
        super(cause);
    }

    protected GetNowUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
