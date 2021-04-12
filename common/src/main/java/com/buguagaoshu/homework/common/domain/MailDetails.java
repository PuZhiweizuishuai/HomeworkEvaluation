package com.buguagaoshu.homework.common.domain;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-12 19:14
 */
public class MailDetails {
    /**
     * 邮件发送者
     * */
    private String from;

    /**
     * 邮件接收人
     * */
    private String to;

    /**
     * 称呼
     * */
    private String name;



    /**
     * 标题
     * */
    private String title;

    /**
     * 正文
     * */
    private String text;

    /**
     * 验证码
     * */
    private String code;

    /**
     * 类型
     * */
    private Integer type;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
