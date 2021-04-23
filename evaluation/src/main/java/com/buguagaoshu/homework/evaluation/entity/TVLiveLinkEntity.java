package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-23 21:48
 */
@Data
@TableName("tv_live_link")
public class TVLiveLinkEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    private String link;
}
