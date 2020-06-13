package com.buguagaoshu.homework.evaluation.vo;

import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-05 11:42
 */
@Data
public class AdminAddUser {
    private String userId;

    private String password;

    private String role;

    private Integer status;

    private String msg;
}
