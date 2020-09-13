package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.HomeworkWithQuestionsEntity;

import java.util.Map;

/**
 * 问题-作业关联表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface HomeworkWithQuestionsService extends IService<HomeworkWithQuestionsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 返回作业与问题关联的 Map
     * @param homeworkId 作业ID
     * @return map
     * */
    Map<Long, HomeworkWithQuestionsEntity> homeworkWithQuestionMap(long homeworkId);
}

