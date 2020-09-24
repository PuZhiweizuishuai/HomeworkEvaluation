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

    public static boolean check(String role, String nowUserRole) {
        String[] strings = nowUserRole.split(",");
        boolean flag = false;
        // 如果是管理员，可以授予所有权限
        for (String s: strings) {
            if (RoleTypeEnum.ADMIN.getRole().equals(s)) {
                return check(role);
            }
            if (RoleTypeEnum.TEACHER.getRole().equals(s)) {
                flag = true;
            }
        }
        // 如果是老师，不能给管理员权限
        if (flag) {
            if (RoleTypeEnum.ADMIN.getRole().equals(role)) {
                return false;
            }
            return check(role);
        }
        // 无权限
        return false;
    }

    public static boolean checkTeacher(String authorities) {
        return ADMIN.getRole().equals(authorities) || TEACHER.getRole().equals(authorities);
    }

    public String getRole() {
        return role;
    }

    public static boolean check(String role) {
        for (RoleTypeEnum r : RoleTypeEnum.values()) {
            if (r.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }

}
