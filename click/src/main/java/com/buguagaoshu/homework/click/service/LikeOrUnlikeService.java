package com.buguagaoshu.homework.click.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.click.entity.LikeOrUnlikeEntity;

import java.util.Map;

/**
 * 点赞或点踩表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2021-03-17 23:32:57
 */
public interface LikeOrUnlikeService extends IService<LikeOrUnlikeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

