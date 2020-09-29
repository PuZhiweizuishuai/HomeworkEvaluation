package com.buguagaoshu.homework.common.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-28 14:19
 * 通知类型
 */
public enum NotificationTypeEnum {
    /**
     * 课程内消息通知
     * 包含 新课件发布，测验发布，课程公告发布，教师批改通知
     * 教师回复，课程内论坛消息等等
     */
    COURSE_BULLETIN(0, "新的课程公告"),

    COURSE_COURSEWARE(1, "新的课件发布"),

    COURSE_EXAM(2, "新的测验发布"),

    COURSE_SUBMIT_EXAM(3, "学生提交作业,提醒教师批改的通知！"),


    COURSE_KEEPER(4, "教师批改作业完成，提醒学生查看的通知!"),

    COURSE_KEEPER_ERROR(5, "作业被打回！"),

    COURSE_JOIN(6, "加入课程的通知！"),

    COURSE_BBS_COMMENT(7, "课程讨论区收到新评论！"),

    /**
     * 留下数字空间，方便之后通知类型扩展
     * */
    BBS_COMMENT(100, "论坛收到回复"),


    BBS_LIKE(101, "论坛收到点赞"),


    BBS_NEW_FANS(102, "论坛收到新粉丝"),

    /**
     * 收到其它人的私信消息
     */
    LETTER(200, "私信"),


    SYSTEM(300, "系统消息"),


    UNREAD(0, "未读"),
    READ(1, "已读");
    int code;
    String msg;

    NotificationTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
