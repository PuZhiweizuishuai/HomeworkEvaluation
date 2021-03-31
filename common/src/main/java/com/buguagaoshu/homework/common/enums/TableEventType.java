package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 14:45
 */
public enum TableEventType {
    /**
     * 表中数据操作类型
     * */
    INSERT(0),
    UPDATE(1),
    DELETE(2);

    int code;

    TableEventType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
