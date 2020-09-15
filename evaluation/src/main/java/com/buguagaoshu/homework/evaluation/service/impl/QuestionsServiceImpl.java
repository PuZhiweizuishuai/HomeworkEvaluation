package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.buguagaoshu.homework.common.domain.CustomPage;
import com.buguagaoshu.homework.common.enums.QuestionTypeEnum;
import com.buguagaoshu.homework.evaluation.entity.HomeworkWithQuestionsEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitHomeworkStatusEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitQuestionsEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
import com.buguagaoshu.homework.evaluation.service.HomeworkWithQuestionsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

import com.buguagaoshu.homework.evaluation.dao.QuestionsDao;
import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;
import com.buguagaoshu.homework.evaluation.service.QuestionsService;
import org.springframework.util.StringUtils;



/**
 * @author puzhiwei
 */
@Service("questionsService")
@Slf4j
public class QuestionsServiceImpl extends ServiceImpl<QuestionsDao, QuestionsEntity> implements QuestionsService {


    private HomeworkWithQuestionsService homeworkWithQuestionsService;


    @Autowired
    public void setHomeworkWithQuestionsService(HomeworkWithQuestionsService homeworkWithQuestionsService) {
        this.homeworkWithQuestionsService = homeworkWithQuestionsService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Claims user) {
        QueryWrapper<QuestionsEntity> wrapper = new QueryWrapper<>();
        String type = (String) params.get("type");
        if (!StringUtils.isEmpty(type)) {
            wrapper.eq("type", type);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.like("question", key);
        }
        wrapper.eq("create_teacher", user.getId()).or().eq("share_status", 1);

        IPage<QuestionsEntity> page = this.page(
                new Query<QuestionsEntity>().getPage(params),
                wrapper
        );
        List<QuestionsModel> serializeMode = serializeAnswer(page.getRecords());
        IPage<QuestionsModel> modelPage = new CustomPage<>(serializeMode, page.getTotal(), page.getSize(), page.getCurrent(), page.orders());
        return new PageUtils(modelPage);
    }

    @Override
    public boolean checkUseQuestionPower(Long questionId, String teacherId) {
        QuestionsEntity questionsEntity = this.getById(questionId);
        if (questionsEntity == null) {
            return false;
        }
        return questionsEntity.getCreateTeacher().equals(teacherId)
                || questionsEntity.getShareStatus() == QuestionTypeEnum.SHARE_QUESTION.getCode();
    }

    @Override
    public List<QuestionsModel> questionModelList(long homeworkId, String userId,
                                                  List<QuestionsEntity> questionsEntityList,
                                                  Map<Long, HomeworkWithQuestionsEntity> questionsMaps,
                                                  Map<Long, SubmitQuestionsEntity> submit,
                                                  boolean rightAnswer) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<QuestionsModel> questionsModels = new ArrayList<>();
        for (QuestionsEntity q : questionsEntityList) {
            try {
                questionsModels.add(questionEntityToModel(q, questionsMaps, submit, objectMapper, rightAnswer));
            } catch (JsonProcessingException e) {
                log.error("题目反序列化失败，请检查id为 {} 的题目!", q.getId());
                throw new UserDataFormatException("题目反序列化失败,请稍后重试！");
            }
        }
        return questionsModels;
    }


    public List<QuestionsModel> serializeAnswer(List<QuestionsEntity> questionsEntities) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<QuestionsModel> questionsModels = new ArrayList<>();
        if (questionsEntities != null) {
            questionsEntities.forEach((q) -> {
                QuestionsModel questionsModel = new QuestionsModel();
                BeanUtils.copyProperties(q, questionsModel);
                if (QuestionTypeEnum.isChoice(q.getType())) {
                    try {
                        questionsModel.setAnswer(objectMapper.readValue(q.getAnswer(), List.class));
                    } catch (JsonProcessingException e) {
                        log.error("问题 {} 的答案反序列化失败，请检查答案格式！", q.getId());
                    }
                }
                questionsModels.add(questionsModel);
            });
        }
        return questionsModels;
    }

    /**
     * 将从数据库获取到的问题实体，转换为前端所需要的问题模型
     * @param rightAnswer 是否给出正确答案
     * */
    private QuestionsModel questionEntityToModel(QuestionsEntity questionsEntity,
                                                 Map<Long, HomeworkWithQuestionsEntity> questionsMaps,
                                                 Map<Long, SubmitQuestionsEntity> submitQuestionsEntityMap,
                                                 ObjectMapper objectMapper,
                                                 boolean rightAnswer) throws JsonProcessingException {
        QuestionsModel questionsModel = new QuestionsModel();
        SubmitQuestionsEntity submitQuestionsEntity = submitQuestionsEntityMap.get(questionsEntity.getId());
        BeanUtils.copyProperties(questionsEntity, questionsModel);
        // 题目分值
        questionsModel.setScore(questionsEntity.getScore());
        // 实际得分
        questionsModel.setRealityScore(submitQuestionsEntity.getScore());
        if (QuestionTypeEnum.isChoice(questionsEntity.getType())) {
            questionsModel.setOptions((List<String>) objectMapper.readValue(questionsEntity.getOptions(), List.class));
            questionsModel.setAnswer((List<String>) objectMapper.readValue(submitQuestionsEntity.getAnswer(), List.class));
            if (rightAnswer) {
                questionsModel.setRightAnswer(questionsEntity.getAnswer());
            }
        } else {
            if (rightAnswer) {
                questionsModel.setRightAnswer(questionsEntity.getOtherAnswer());
            }
        }
        questionsModel.setTeacherComment(submitQuestionsEntity.getTeacherComment());
        questionsModel.setScore(questionsMaps.get(questionsEntity.getId()).getScore());
        questionsModel.setOtherAnswer(submitQuestionsEntity.getOtherAnswer());
        return questionsModel;
    }
}
