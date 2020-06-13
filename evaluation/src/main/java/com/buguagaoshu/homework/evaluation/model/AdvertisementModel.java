package com.buguagaoshu.homework.evaluation.model;

import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-10 16:46
 */
@Data
public class AdvertisementModel {
    private Long id;
    /**
     * 广告标题
     */
    private String title;

    /**
     * 点击后要链接到的地址
     */
    private String url;

    /**
     * 显示的图片地址
     */
    private String image;

    /**
     * 开始投放时间
     */
    private String startTime;

    /**
     * 结束投放时间
     */
    private Integer endTime;


    /**
     * 类型，显示位置【
     0 首页顶部大图，
     1 课程页顶部大图
     2 首页广告
     3 课程页广告
     】
     */
    private Integer type;

    /**
     * 状态【0 未启用， 1 启用】
     */
    private Integer status;
}
