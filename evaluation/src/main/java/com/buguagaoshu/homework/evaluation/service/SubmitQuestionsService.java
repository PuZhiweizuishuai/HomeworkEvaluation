package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitQuestionsEntity;
import com.buguagaoshu.homework.evaluation.model.HomeworkAnswer;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

/**
 * 用户提交的答案保存
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-16 18:47:06
 */
public interface SubmitQuestionsService extends IService<SubmitQuestionsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 在用户第一次进入问题页时，初始化所有问题和答案，并记录进入时间
     *
     * @param questionsEntityList 作业问题列表
     * @param id             用户ID
     * @param homeworkEntity 作业信息
     * @throws JsonProcessingException 将答案序列化成json的异常
     * @return 初始化的用户提交答案列表
     */
    Map<Long ,SubmitQuestionsEntity> saveQuestions(List<QuestionsEntity> questionsEntityList, String id, HomeworkEntity homeworkEntity) throws JsonProcessingException;


    /**
     * 保存并判断用户提交的答案
     *
     * @param homeworkAnswer 作业答案列表
     * @param id             用户ID
     * @param homeworkEntity 作业信息
     * @throws JsonProcessingException Json 序列化异常
     * @return 分数
     */
    double saveWithJudgeQuestions(HomeworkAnswer homeworkAnswer, String id, HomeworkEntity homeworkEntity) throws JsonProcessingException;

    /**
     * 更新用户保存的答案
     *
     * @param homeworkAnswer 作业答案列表
     * @param id             用户ID
     * @param homeworkEntity 作业信息
     */
    void updateSaveQuestions(HomeworkAnswer homeworkAnswer, String id, HomeworkEntity homeworkEntity);

    /**
     * 更新保存并判断用户提交的答案
     *
     * @param homeworkAnswer 作业答案列表
     * @param id             用户ID
     * @param homeworkEntity 作业信息
     * @return 分数
     * */
    double updateSubmitJudgeQuestion(HomeworkAnswer homeworkAnswer, String id, HomeworkEntity homeworkEntity) throws JsonProcessingException;


    /**
     * 查找当前用户的提交信息
     *
     * @param userId 用户 ID
     * @param homeworkEntity 作业信息
     * @return 结果
     * */
    Map<Long, SubmitQuestionsEntity> findUserSubmit(String userId, HomeworkEntity homeworkEntity);
}

