package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 *
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-10 16:31:59
 */
@Data
@TableName("advertisement")
public class AdvertisementEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
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
     * 创建时间
     */

    private Long createTime;

    /**
     * 创建的用户
     */
    private String createUser;

    /**
     * 开始投放时间
     */

    private Long startTime;

    /**
     * 结束投放时间
     */

    private Long endTime;

    /**
     * 更新时间
     */

    private Long updateTime;

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
     * 点击次数
     */
    private Long viewCount;


}
