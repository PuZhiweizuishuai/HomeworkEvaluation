package com.buguagaoshu.homework.evaluation.vo;

import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-06 11:54
 */
@Data
public class AlterUserStatus {
    private String userId;

    private Integer status;

    private Long time;
}
