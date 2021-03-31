package com.buguagaoshu.homework.common.search;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 23:06
 */
public class QuestionsSearchMapper {
    private Long id;

    /**
     * 题目
     */
    private String question;

    /**
     * 类型 【0 单选  1 多选  2 填空  3 问答】
     */
    private Integer type;

    /**
     * 答案，保存为json对象，方便判断
     * 选择题答案
     */
    private String answer;

    /**
     * 问答题，填空题答案
     * */
    private String otherAnswer;

    private String options;

    /**
     * 提示
     */
    private String tips;
    /**
     * 是否分享 【0 私有  1 其它老师可见 2 所有人可见】
     */
    private Integer shareStatus;

    /**
     * 创建老师
     */
    private String createTeacher;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 提交次数
     */
    private Long submitCount;

    /**
     * 正确次数
     */
    private Long rightCount;


    /**
     * 难度 0 ---- 10
     * */
    private Integer difficulty;


    /**
     * 标签
     * */
    private String tag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOtherAnswer() {
        return otherAnswer;
    }

    public void setOtherAnswer(String otherAnswer) {
        this.otherAnswer = otherAnswer;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public Integer getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(Integer shareStatus) {
        this.shareStatus = shareStatus;
    }

    public String getCreateTeacher() {
        return createTeacher;
    }

    public void setCreateTeacher(String createTeacher) {
        this.createTeacher = createTeacher;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getSubmitCount() {
        return submitCount;
    }

    public void setSubmitCount(Long submitCount) {
        this.submitCount = submitCount;
    }

    public Long getRightCount() {
        return rightCount;
    }

    public void setRightCount(Long rightCount) {
        this.rightCount = rightCount;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
