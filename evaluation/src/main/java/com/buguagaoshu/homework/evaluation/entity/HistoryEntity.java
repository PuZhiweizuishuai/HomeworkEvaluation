package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import lombok.Data;

/**
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-26 21:37:00
 */
@Data
@TableName("history")
public class HistoryEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上次查看的课件ID
     */
    private Long coursewareId;

    /**
     * 查看的学生
     */
    private Long studentId;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 如果是视频的话，视频进度
     */
    private Double progressBar;

}
