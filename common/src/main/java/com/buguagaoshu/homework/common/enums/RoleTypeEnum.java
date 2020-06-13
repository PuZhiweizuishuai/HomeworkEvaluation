package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 13:44
 * 角色
 */
public enum  RoleTypeEnum {
    /**
     * 角色
     * */
    ADMIN("ROLE_ADMIN"),
    TEACHER("ROLE_TEACHER"),
    ASSISTANT("ROLE_ASSISTANT"),
    STUDENT("ROLE_STUDENT"),
    USER("ROLE_USER")
    ;

    String role;

    RoleTypeEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static boolean check(String role) {
        for (RoleTypeEnum r : RoleTypeEnum.values()) {
            if (role.equals(r.getRole())) {
                return true;
            }
        }
        return false;
    }

}
