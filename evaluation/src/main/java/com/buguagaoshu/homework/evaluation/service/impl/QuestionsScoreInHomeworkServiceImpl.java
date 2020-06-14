package com.buguagaoshu.homework.evaluation.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.QuestionsScoreInHomeworkDao;
import com.buguagaoshu.homework.evaluation.entity.QuestionsScoreInHomeworkEntity;
import com.buguagaoshu.homework.evaluation.service.QuestionsScoreInHomeworkService;


@Service("questionsScoreInHomeworkService")
public class QuestionsScoreInHomeworkServiceImpl extends ServiceImpl<QuestionsScoreInHomeworkDao, QuestionsScoreInHomeworkEntity> implements QuestionsScoreInHomeworkService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<QuestionsScoreInHomeworkEntity> page = this.page(
                new Query<QuestionsScoreInHomeworkEntity>().getPage(params),
                new QueryWrapper<QuestionsScoreInHomeworkEntity>()
        );

        return new PageUtils(page);
    }

}