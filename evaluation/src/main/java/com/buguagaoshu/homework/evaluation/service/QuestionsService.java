package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;

import java.util.Map;

/**
 * 问题表，需要与作业表关联
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface QuestionsService extends IService<QuestionsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

