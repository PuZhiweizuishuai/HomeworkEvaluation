package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.InviteCodeUseLogEntity;

import java.util.Map;

/**
 * 邀请码使用列表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface InviteCodeUseLogService extends IService<InviteCodeUseLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

