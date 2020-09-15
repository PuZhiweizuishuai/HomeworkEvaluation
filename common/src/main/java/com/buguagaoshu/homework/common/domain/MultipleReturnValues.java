package com.buguagaoshu.homework.common.domain;

import java.util.HashMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-15 20:09
 * 多个返回值处理
 */
public class MultipleReturnValues extends HashMap<String, Object> {
    @Override
    public MultipleReturnValues put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static MultipleReturnValues ok() {
        return new MultipleReturnValues();
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }
}
