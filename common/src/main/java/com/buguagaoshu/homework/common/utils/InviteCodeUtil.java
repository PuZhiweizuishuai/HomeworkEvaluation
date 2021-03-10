package com.buguagaoshu.homework.common.utils;

import java.util.UUID;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 14:46
 * 生成邀请码
 */
public class InviteCodeUtil {
    public static String createInviteCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
