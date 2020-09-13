package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.HomeworkWithQuestionsEntity;
import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitHomeworkStatusEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitQuestionsEntity;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
import io.jsonwebtoken.Claims;

import java.util.List;
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



    /**
     * 获取作业下的问题列表
     * @param homeworkId 作业ID
     * @param userId 学生ID
     * @param questionsEntityList 问题列表
     * @param questionsMaps 问题与作业关联数据
     * @param submit 作业提交数据
     * @param rightAnswer 是否给出正确答案
     * @return 问题列表
     * */
    List<QuestionsModel> questionModelList(long homeworkId,
                                           String userId,
                                           List<QuestionsEntity> questionsEntityList,
                                           Map<Long, HomeworkWithQuestionsEntity> questionsMaps,
                                           Map<Long, SubmitQuestionsEntity> submit,
                                           boolean rightAnswer);
}

