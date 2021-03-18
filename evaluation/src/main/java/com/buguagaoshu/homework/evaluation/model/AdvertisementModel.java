package com.buguagaoshu.homework.evaluation.model;

import com.buguagaoshu.homework.common.valid.ListValue;
import com.buguagaoshu.homework.common.valid.LongJsonDeserializer;
import com.buguagaoshu.homework.common.valid.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-10 16:46
 */
@Data
public class AdvertisementModel {
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;
    /**
     * 广告标题
     */
    @NotBlank(message = "标题不能为空！")
    private String title;

    /**
     * 点击后要链接到的地址
     */
    @NotBlank(message = "链接不能为空")
    private String url;

    /**
     * 显示的图片地址
     */
    @NotBlank(message = "图片地址不能为空！")
    private String image;

    /**
     * 开始投放时间
     */

    private String startTime;

    /**
     * 结束时间
     */

    private String endTime;


    /**
     * 类型，显示位置【
     * 0 首页顶部大图，
     * 1 课程页顶部大图
     * 2 首页广告
     * 3 课程页广告
     * 】
     */
    @ListValue(value = {0, 1, 2, 3}, message = "类型取值非法！")
    private Integer type;

    /**
     * 状态【0 未启用， 1 启用】
     */
    private Integer status;
}
