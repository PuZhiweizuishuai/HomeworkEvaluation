package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.QuestionTypeEnum;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.entity.HomeworkWithQuestionsEntity;
import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;
import com.buguagaoshu.homework.evaluation.model.HomeworkAnswer;
import com.buguagaoshu.homework.evaluation.model.UserSubmitQuestion;
import com.buguagaoshu.homework.evaluation.service.HomeworkWithQuestionsService;
import com.buguagaoshu.homework.evaluation.service.QuestionsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.SubmitQuestionsDao;
import com.buguagaoshu.homework.evaluation.entity.SubmitQuestionsEntity;
import com.buguagaoshu.homework.evaluation.service.SubmitQuestionsService;

/**
 * @author Pu Zhiwei
 */
@Service("submitQuestionsService")
public class SubmitQuestionsServiceImpl extends ServiceImpl<SubmitQuestionsDao, SubmitQuestionsEntity> implements SubmitQuestionsService {

    private QuestionsService questionsService;

    private HomeworkWithQuestionsService homeworkWithQuestionsService;

    @Autowired
    public void setHomeworkWithQuestionsService(HomeworkWithQuestionsService homeworkWithQuestionsService) {
        this.homeworkWithQuestionsService = homeworkWithQuestionsService;
    }

    @Autowired
    public void setQuestionsService(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SubmitQuestionsEntity> page = this.page(
                new Query<SubmitQuestionsEntity>().getPage(params),
                new QueryWrapper<SubmitQuestionsEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveQuestions(HomeworkAnswer homeworkAnswer, String id, HomeworkEntity homeworkEntity) throws JsonProcessingException {
        // 要保存的作业列表
        List<SubmitQuestionsEntity> submitQuestionsEntities = new ArrayList<>();
        // 问题列表
        List<QuestionsEntity> questionsEntityList = getQuestionList(homeworkEntity.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Long, UserSubmitQuestion> submitQuestionsEntityMap = homeworkAnswer.getAnswers().stream()
                .collect(Collectors.toMap(UserSubmitQuestion::getQuestionId, q -> q));


        // 存入问题列表
        for (QuestionsEntity question : questionsEntityList) {
            UserSubmitQuestion userSubmitQuestion = submitQuestionsEntityMap.get(question.getId());
            SubmitQuestionsEntity s = build(question, userSubmitQuestion, id, homeworkEntity.getId(), 0.0, objectMapper);
            s.setCreateTime(System.currentTimeMillis());
            submitQuestionsEntities.add(s);
        }
        this.saveBatch(submitQuestionsEntities);
    }

    @Override
    public double saveWithJudgeQuestions(HomeworkAnswer homeworkAnswer, String id, HomeworkEntity homeworkEntity) throws JsonProcessingException {
        List<SubmitQuestionsEntity> submitQuestionsEntities = new ArrayList<>();
        List<HomeworkWithQuestionsEntity> homeworkWithQuestionsEntities =
                homeworkWithQuestionsService.list(
                        new QueryWrapper<HomeworkWithQuestionsEntity>().eq("homework_id", homeworkEntity.getId())
                );
        Map<Long, HomeworkWithQuestionsEntity> homeworkWithQuestionsEntityMap
                = homeworkWithQuestionsEntities.stream().collect(Collectors.toMap(HomeworkWithQuestionsEntity::getQuestionId, h -> h));
        // 问题列表
        List<QuestionsEntity> questionsEntityList = questionsService.listByIds(homeworkWithQuestionsEntityMap.keySet());


        // 将提交的答案数据映射为 Map
        Map<Long, UserSubmitQuestion> submitQuestionsEntityMap = homeworkAnswer.getAnswers().stream()
                .collect(Collectors.toMap(UserSubmitQuestion::getQuestionId, q -> q));
        ObjectMapper objectMapper = new ObjectMapper();
        // 存入问题列表并 判断答案
        double getScore = 0;
        for (QuestionsEntity question : questionsEntityList) {
            // TODO 修改加 1 操作,直接从数据库加，这种加一容易造成数据不一
            question.setSubmitCount(question.getSubmitCount() + 1);
            UserSubmitQuestion submit = submitQuestionsEntityMap.get(question.getId());
            // 没有这道题目数据直接判 0 分
            if (submit == null) {
                SubmitQuestionsEntity s = build(question, submit, id, homeworkEntity.getId(), 0.0, objectMapper);
                s.setCreateTime(System.currentTimeMillis());
                submitQuestionsEntities.add(s);
            } else {
                // 选择题判断题
                double score = 0.0;
                if (QuestionTypeEnum.isChoice(question.getType()) || question.getType() == QuestionTypeEnum.JUDGE.getCode()) {
                    int goodScore = homeworkWithQuestionsEntityMap.get(question.getId()).getScore();
                    score =
                            judgeQuestion(submit, question, goodScore, homeworkEntity.getSourceType(), objectMapper);
                }
                SubmitQuestionsEntity s = build(question, submit, id, homeworkEntity.getId(), score, objectMapper);
                s.setCreateTime(System.currentTimeMillis());
                submitQuestionsEntities.add(s);
                getScore += score;
            }
        }

        // 保存用户提交的答案
        this.saveBatch(submitQuestionsEntities);
        // TODO 通知教师
        // 更新题目正确错误数据
        questionsService.updateBatchById(questionsEntityList);
        return getScore;
    }

    @Override
    public void updateSaveQuestions(HomeworkAnswer homeworkAnswer, String id, HomeworkEntity homeworkEntity) {
        List<SubmitQuestionsEntity> submitQuestionsEntities =
                this.list(
                        new QueryWrapper<SubmitQuestionsEntity>()
                                .eq("user_id", id)
                                .eq("homework_id", homeworkEntity.getId())
                );
//        Map<Long, SubmitQuestionsEntity> questionsEntityMap = submitQuestionsEntities.stream()
//                .collect(Collectors.toMap(SubmitQuestionsEntity::getQuestionId, q->q));
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Long, UserSubmitQuestion> submitQuestionMap = homeworkAnswer.getAnswers().stream()
                .collect(Collectors.toMap(UserSubmitQuestion::getQuestionId, q->q));
        for (SubmitQuestionsEntity submitQuestionsEntity : submitQuestionsEntities) {
            UserSubmitQuestion userSubmitQuestion = submitQuestionMap.get(submitQuestionsEntity.getQuestionId());
            if (userSubmitQuestion != null) {
                if (userSubmitQuestion.getAnswer() != null) {
                    try {
                        submitQuestionsEntity.setAnswer(objectMapper.writeValueAsString(userSubmitQuestion.getAnswer()));
                    } catch (JsonProcessingException e) {
                        submitQuestionsEntity.setAnswer("[\"\"]");
                    }
                }
                submitQuestionsEntity.setUpdateTime(System.currentTimeMillis());
                submitQuestionsEntity.setOtherAnswer(userSubmitQuestion.getOtherAnswer());
            }
        }
        this.updateBatchById(submitQuestionsEntities);
    }

    @Override
    public double updateSubmitJudgeQuestion(HomeworkAnswer homeworkAnswer, String id, HomeworkEntity homeworkEntity) throws JsonProcessingException {
        // 上次保存的数据
        List<SubmitQuestionsEntity> submitQuestionsEntities =
                this.list(
                        new QueryWrapper<SubmitQuestionsEntity>()
                                .eq("user_id", id)
                                .eq("homework_id", homeworkEntity.getId())
                );
        Map<Long, SubmitQuestionsEntity> submitQuestionsEntityMap =
                submitQuestionsEntities.stream().collect(Collectors.toMap(SubmitQuestionsEntity::getQuestionId, q->q));
        List<HomeworkWithQuestionsEntity> homeworkWithQuestionsEntities =
                homeworkWithQuestionsService.list(
                        new QueryWrapper<HomeworkWithQuestionsEntity>().eq("homework_id", homeworkEntity.getId())
                );
        Map<Long, HomeworkWithQuestionsEntity> homeworkWithQuestionsEntityMap
                = homeworkWithQuestionsEntities.stream().collect(Collectors.toMap(HomeworkWithQuestionsEntity::getQuestionId, h -> h));
        // 问题列表
        List<QuestionsEntity> questionsEntityList = questionsService.listByIds(homeworkWithQuestionsEntityMap.keySet());


        // 将提交的答案数据映射为 Map
        Map<Long, UserSubmitQuestion> submitMap = homeworkAnswer.getAnswers().stream()
                .collect(Collectors.toMap(UserSubmitQuestion::getQuestionId, q -> q));
        ObjectMapper objectMapper = new ObjectMapper();
        double getScore = 0;
        for (QuestionsEntity question : questionsEntityList) {
            question.setSubmitCount(question.getSubmitCount() + 1);
            UserSubmitQuestion submit = submitMap.get(question.getId());
            if (submit != null) {
                double score = 0.0;
                if (QuestionTypeEnum.isChoice(question.getType()) || question.getType() == QuestionTypeEnum.JUDGE.getCode()) {
                    int goodScore = homeworkWithQuestionsEntityMap.get(question.getId()).getScore();
                    score =
                            judgeQuestion(submit, question, goodScore, homeworkEntity.getSourceType(), objectMapper);
                    SubmitQuestionsEntity submitQuestionsEntity = submitQuestionsEntityMap.get(question.getId());
                    submitQuestionsEntity.setAnswer(objectMapper.writeValueAsString(submit.getAnswer()));
                    submitQuestionsEntity.setUpdateTime(System.currentTimeMillis());
                    submitQuestionsEntity.setScore(score);
                    getScore += score;
                } else {
                    SubmitQuestionsEntity submitQuestionsEntity = submitQuestionsEntityMap.get(question.getId());
                    submitQuestionsEntity.setUpdateTime(System.currentTimeMillis());
                    submitQuestionsEntity.setOtherAnswer(submit.getOtherAnswer());
                }
            }

        }

        this.updateBatchById(submitQuestionsEntityMap.values());
        // 更新题目正确错误数据
        questionsService.updateBatchById(questionsEntityList);
        return getScore;
    }


    /**
     * 判题,返回分数
     *
     * @param submitQuestion  用户提交的答案
     * @param questionsEntity 标准答案
     * @param score           满分
     * @param judgeType       多选判题策略
     *                        0 给一半
     *                        1 不给
     * @param objectMapper    将string 转list
     */
    public double judgeQuestion(UserSubmitQuestion submitQuestion,
                                QuestionsEntity questionsEntity,
                                int score, int judgeType,
                                ObjectMapper objectMapper) throws JsonProcessingException {
        // 单选判题策略
        if (questionsEntity.getType() == QuestionTypeEnum.SINGLE_CHOICE.getCode()) {
            List<String> answer = objectMapper.readValue(questionsEntity.getAnswer(), List.class);
            if (submitQuestion.getAnswer() != null && submitQuestion.getAnswer().size() != 0) {
                if (submitQuestion.getAnswer().size() > 1) {
                    return 0.0;
                }
                if (answer.contains(submitQuestion.getAnswer().get(0))) {
                    // TODO 修改加 1 操作
                    questionsEntity.setRightCount(questionsEntity.getRightCount() + 1);
                    return score;
                }
            } else {
                return 0.0;
            }
        }
        // 多选题
        if (questionsEntity.getType() == QuestionTypeEnum.MULTIPLE_CHOICE.getCode()) {
            List<String> answer = objectMapper.readValue(questionsEntity.getAnswer(), List.class);
            if (submitQuestion.getAnswer() != null && submitQuestion.getAnswer().size() != 0) {
                Map<String, String> map = answer.stream().collect(Collectors.toMap(m->m,m->m));
                int size = 0;
                for (String str : submitQuestion.getAnswer()) {
                    if (!str.equals(map.get(str))) {
                        return 0.0;
                    }
                    size++;
                }
                if (size == answer.size()) {
                    // TODO 修改加 1 操作
                    questionsEntity.setRightCount(questionsEntity.getRightCount() + 1);
                    return score;
                }
                if (judgeType == 0) {
                    return score / 2;
                }
                return 0.0;
            }
        }

        // 判断题策略
        if (questionsEntity.getType() == QuestionTypeEnum.JUDGE.getCode()) {
            if (questionsEntity.getOtherAnswer().equals(submitQuestion.getOtherAnswer())) {
                // TODO 修改加 1 操作
                questionsEntity.setRightCount(questionsEntity.getRightCount() + 1);
                return score;
            }
            return 0.0;
        }
        return 0.0;
    }


    private List<QuestionsEntity> getQuestionList(Long homeworkId) {
        List<HomeworkWithQuestionsEntity> homeworkWithQuestionsEntities =
                homeworkWithQuestionsService.list(
                        new QueryWrapper<HomeworkWithQuestionsEntity>().eq("homework_id", homeworkId)
                );
        Map<Long, HomeworkWithQuestionsEntity> homeworkWithQuestionsEntityMap
                = homeworkWithQuestionsEntities.stream().collect(Collectors.toMap(HomeworkWithQuestionsEntity::getQuestionId, h -> h));

        return questionsService.listByIds(homeworkWithQuestionsEntityMap.keySet());
    }


    private SubmitQuestionsEntity build(QuestionsEntity questionsEntity,
                                        UserSubmitQuestion userSubmitQuestion,
                                        String userId,
                                        long homeworkId,
                                        double score,
                                        ObjectMapper mapper) throws JsonProcessingException {
        SubmitQuestionsEntity submitQuestionsEntity = new SubmitQuestionsEntity();
        submitQuestionsEntity.setQuestionId(questionsEntity.getId());
        submitQuestionsEntity.setUserId(userId);
        submitQuestionsEntity.setHomeworkId(homeworkId);
        if (userSubmitQuestion != null) {
            if (QuestionTypeEnum.isChoice(questionsEntity.getType())) {
                submitQuestionsEntity.setAnswer(mapper.writeValueAsString(userSubmitQuestion.getAnswer()));
            } else {
                submitQuestionsEntity.setOtherAnswer(userSubmitQuestion.getOtherAnswer());
            }
        }
        submitQuestionsEntity.setScore(score);
        submitQuestionsEntity.setUpdateTime(System.currentTimeMillis());
        return submitQuestionsEntity;
    }

}
