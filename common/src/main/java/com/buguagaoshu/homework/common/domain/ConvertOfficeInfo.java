package com.buguagaoshu.homework.common.domain;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 15:49
 */
public class ConvertOfficeInfo {
    /**
     * 原文件名
     * */
    private String filename;

    /**
     * 原文件地址
     */
    private String filePath;


    /**
     * 输出文件夹
     * */
    private String targetFilePath;


    /**
     * 课件ID
     * */
    private Long coursewareId;


    private String userID;

    private String username;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    public Long getCoursewareId() {
        return coursewareId;
    }

    public void setCoursewareId(Long coursewareId) {
        this.coursewareId = coursewareId;
    }
}
