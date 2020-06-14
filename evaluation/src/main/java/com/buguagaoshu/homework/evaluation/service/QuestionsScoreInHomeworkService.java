package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.QuestionsScoreInHomeworkEntity;

import java.util.Map;

/**
 * 负责关联每次作业中每道题目的分值
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-14 20:42:02
 */
public interface QuestionsScoreInHomeworkService extends IService<QuestionsScoreInHomeworkEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

