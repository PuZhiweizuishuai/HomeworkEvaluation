package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.CustomPage;
import com.buguagaoshu.homework.common.enums.QuestionTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.HomeworkWithQuestionsEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitQuestionsEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
import com.buguagaoshu.homework.evaluation.service.HomeworkWithQuestionsService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.QuestionsDao;
import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;
import com.buguagaoshu.homework.evaluation.service.QuestionsService;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


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
        // if (questionsEntity.getCreateTeacher().equals(teacherId) || (questionsEntity.getShareStatus() == QuestionTypeEnum.SHARE_QUESTION.getCode() && ))
        // TODO 目测应该再判断一下教师与管理员权限
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

    @Override
    public Boolean saveQuestion(QuestionsModel questionsModel, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        questionHandleValid(questionsModel);
        try {
            this.save(copyQuestionModeToEntity(questionsModel, user.getId()));
            return true;
        } catch (Exception e) {
            log.error("用户： {} 保存问题时出现异常，异常原因是： {}", user.getId(), e.getMessage());
            return false;
        }
    }

    @Override
    public void questionHandleValid(QuestionsModel questionsModel) {
        // 单选和多选题目处理
        if (QuestionTypeEnum.SINGLE_CHOICE.getCode() == questionsModel.getType() ||
                QuestionTypeEnum.MULTIPLE_CHOICE.getCode() == questionsModel.getType()) {
            if (questionsModel.getOptions() == null || questionsModel.getOptions().size() == 0) {
                throw new UserDataFormatException("选择题题目选项设置错误！");
            }
            if (questionsModel.getAnswer() == null || questionsModel.getAnswer().size() == 0) {
                throw new UserDataFormatException("选择题题目答案设置错误！");
            }
            // 判断题答案处理
        } else if (QuestionTypeEnum.JUDGE.getCode() == questionsModel.getType()) {
            try {
                int ans = Integer.parseInt(questionsModel.getOtherAnswer());
                if (ans != QuestionTypeEnum.JUDGE_RIGHT.getCode() && ans != QuestionTypeEnum.JUDGE_ERROR.getCode()) {
                    throw new UserDataFormatException("判断题答案设置错误！");
                }
            } catch (Exception e) {
                throw new UserDataFormatException("判断题答案设置错误！");
            }
        }
        // 标签检查
        if (questionsModel.getTag() != null && questionsModel.getTag().size() > 6) {
            throw new UserDataFormatException("标签不能超过 6 个");
        }
    }

    @Override
    public QuestionsEntity copyQuestionModeToEntity(QuestionsModel questionsModel, String teacher) throws JsonProcessingException {
        QuestionsEntity questionsEntity = new QuestionsEntity();
        questionsEntity.setQuestion(questionsModel.getQuestion());
        questionsEntity.setCreateTeacher(teacher);
        questionsEntity.setDifficulty(questionsModel.getDifficulty());
        questionsEntity.setCreateTime(System.currentTimeMillis());
        questionsEntity.setShareStatus(questionsModel.getShareStatus());
        questionsEntity.setTips(questionsModel.getTips());
        questionsEntity.setType(questionsModel.getType());
        ObjectMapper objectMapper = new ObjectMapper();
        // 写入标签
        try {
            questionsEntity.setTag(objectMapper.writeValueAsString(questionsModel.getTag()));
        } catch (JsonProcessingException ignored) {}

        if (questionsModel.getType() == QuestionTypeEnum.MULTIPLE_CHOICE.getCode()
                || questionsModel.getType() == QuestionTypeEnum.SINGLE_CHOICE.getCode()) {
            questionsEntity.setAnswer(objectMapper.writeValueAsString(questionsModel.getAnswer()));
            questionsEntity.setOptions(objectMapper.writeValueAsString(questionsModel.getOptions()));
        } else {
            questionsEntity.setOtherAnswer(questionsModel.getOtherAnswer());
        }
        return questionsEntity;
    }

    @Override
    public Boolean updateQuestion(QuestionsModel questionsModel, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        if (questionsModel.getId() == null) {
            return false;
        }
        QuestionsEntity questionsEntity = this.getById(questionsModel.getId());
        if (questionsEntity.getCreateTeacher().equals(user.getId())) {
            questionHandleValid(questionsModel);
            try {
                Long id = questionsEntity.getId();
                questionsEntity = copyQuestionModeToEntity(questionsModel, user.getId());
                questionsEntity.setSubmitCount(null);
                questionsEntity.setRightCount(null);
                questionsEntity.setCreateTime(null);
                questionsEntity.setId(id);
                this.updateById(questionsEntity);
                return true;
            } catch (Exception e) {
                log.error("用户 {} 更新问题 {} 的数据出现异常，异常原因： {}, 提交的数据: {}", user.getId(), questionsEntity.getId(), e.getMessage(), questionsModel.toString());
                return false;
            }
        } else {
            return false;
        }
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
                        questionsModel.setOptions(objectMapper.readValue(q.getOptions(), List.class));
                    } catch (JsonProcessingException e) {
                        log.error("问题 {} 的答案反序列化失败，请检查答案格式！", q.getId());
                    }
                }
                try {
                    if (!StringUtils.isEmpty(q.getTag())) {
                        questionsModel.setTag(objectMapper.readValue(q.getTag(), List.class));
                    }
                } catch (JsonProcessingException e) {
                    log.error("问题 {} 的标签反序列化失败，请检查答案格式！", q.getId());
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
        try {
            if (!StringUtils.isEmpty(questionsEntity.getTag())) {
                questionsModel.setTag((List<String>) objectMapper.readValue(questionsEntity.getTag(), List.class));
            }
        } catch (JsonProcessingException ignored) {}

        questionsModel.setTeacherComment(submitQuestionsEntity.getTeacherComment());
        questionsModel.setScore(questionsMaps.get(questionsEntity.getId()).getScore());
        questionsModel.setOtherAnswer(submitQuestionsEntity.getOtherAnswer());
        return questionsModel;
    }
}
