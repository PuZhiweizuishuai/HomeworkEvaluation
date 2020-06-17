package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;
import io.jsonwebtoken.Claims;

import java.util.Map;

/**
 * 问题表，需要与作业表关联
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface QuestionsService extends IService<QuestionsEntity> {

    PageUtils queryPage(Map<String, Object> params,  Claims user);

    /**
     * 检查有没有导入这道题的权限
     * @param questionId 问题 ID
     * @param teacherId 教师 ID
     * @return 结果
     * */
    boolean checkUseQuestionPower(Long questionId, String teacherId);
}

