package com.buguagaoshu.homework.common.exception;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-09 14:39
 * 文件路径异常
 */
public class FilePathException extends RuntimeException {
    public FilePathException() {
    }

    public FilePathException(String message) {
        super(message);
    }

    public FilePathException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilePathException(Throwable cause) {
        super(cause);
    }

    public FilePathException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
