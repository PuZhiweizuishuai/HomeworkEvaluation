package com.buguagaoshu.homework.evaluation.vo;

import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-15 19:40
 * 问题的打分与评价
 */
@Data
public class QuestionComment {
    /**
     * 评价内容
     * */
    private String text;

    /**
     * 问题类型
     * */
    private Integer type;

    /***
     * 问题ID
     *
     */
    private Long id;

    /**
     * 获得成绩
     * */
    private Double score;


    private Integer number;
}
