package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.buguagaoshu.homework.common.enums.QuestionTypeEnum;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

import javax.xml.crypto.Data;


/**
 * @author puzhiwei
 */
@Service("questionsService")
@Slf4j
public class QuestionsServiceImpl extends ServiceImpl<QuestionsDao, QuestionsEntity> implements QuestionsService {

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
        IPage<QuestionsModel> modelIPage = new IPage<QuestionsModel>() {
            @Override
            public List<OrderItem> orders() {
                return null;
            }

            @Override
            public List<QuestionsModel> getRecords() {
                return serializeMode;
            }

            @Override
            public IPage<QuestionsModel> setRecords(List<QuestionsModel> records) {
                return null;
            }

            @Override
            public long getTotal() {
                return page.getTotal();
            }

            @Override
            public IPage<QuestionsModel> setTotal(long total) {
                return null;
            }

            @Override
            public long getSize() {
                return page.getSize();
            }

            @Override
            public IPage<QuestionsModel> setSize(long size) {
                return null;
            }

            @Override
            public long getCurrent() {
                return page.getCurrent();
            }

            @Override
            public IPage<QuestionsModel> setCurrent(long current) {
                return null;
            }
        };
        return new PageUtils(modelIPage);
    }

    @Override
    public boolean checkUseQuestionPower(Long questionId, String teacherId) {
        QuestionsEntity questionsEntity = this.getById(questionId);
        if (questionsEntity == null) {
            return false;
        }
        if (questionsEntity.getCreateTeacher().equals(teacherId)
                || questionsEntity.getShareStatus() == QuestionTypeEnum.SHARE_QUESTION.getCode()) {
            return true;
        }
        return false;
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

}
