package com.buguagaoshu.homework.common.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-13 18:53
 */
public class CustomPage<T> implements IPage<T> {
    private List<T> records;

    private long total;

    private long size;

    private long current;

    private List<OrderItem> orderItems;


    public CustomPage(List<T> records, long total, long size, long current, List<OrderItem> orderItems) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.orderItems = orderItems;
    }

    @Override
    public List<OrderItem> orders() {
        return this.orderItems;
    }

    @Override
    public List<T> getRecords() {
        return this.records;
    }

    @Override
    public IPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public long getTotal() {
        return this.total;
    }

    @Override
    public IPage<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public IPage<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public long getCurrent() {
        return this.current;
    }

    @Override
    public IPage<T> setCurrent(long current) {
        this.current = current;
        return this;
    }
}
