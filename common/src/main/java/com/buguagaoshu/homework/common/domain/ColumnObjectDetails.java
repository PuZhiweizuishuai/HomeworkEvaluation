package com.buguagaoshu.homework.common.domain;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-02-27 23:43
 * 负责传递更新的表格数据
 */

public class ColumnObjectDetails {
    /**
     * 操作类型
     * */
    private Integer eventType;

    /**
     * 表格名
     * */
    private String tableName;


    /**
     * 更新的列名
     * */
    private String[] updateColumnName;


    /**
     * 表格对象
     * */
    private ConcurrentHashMap<String, Object> table;

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String[] getUpdateColumnName() {
        return updateColumnName;
    }

    public void setUpdateColumnName(String[] updateColumnName) {
        this.updateColumnName = updateColumnName;
    }

    public ConcurrentHashMap<String, Object> getTable() {
        return table;
    }

    public void setTable(ConcurrentHashMap<String, Object> table) {
        this.table = table;
    }
}
