package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.InviteCodeEntity;

import java.util.Map;

/**
 * 邀请码
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface InviteCodeService extends IService<InviteCodeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

