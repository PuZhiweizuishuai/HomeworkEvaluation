package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.NotificationEntity;

import java.util.Map;

/**
 * 通知表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-26 21:36:59
 */
public interface NotificationService extends IService<NotificationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

