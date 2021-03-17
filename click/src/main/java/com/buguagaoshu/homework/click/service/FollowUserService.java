package com.buguagaoshu.homework.click.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.click.entity.FollowUserEntity;

import java.util.Map;

/**
 * 用户关注表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2021-03-17 23:32:57
 */
public interface FollowUserService extends IService<FollowUserEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

