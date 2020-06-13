package com.buguagaoshu.homework.evaluation.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.QuestionsDao;
import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;
import com.buguagaoshu.homework.evaluation.service.QuestionsService;


@Service("questionsService")
public class QuestionsServiceImpl extends ServiceImpl<QuestionsDao, QuestionsEntity> implements QuestionsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<QuestionsEntity> page = this.page(
                new Query<QuestionsEntity>().getPage(params),
                new QueryWrapper<QuestionsEntity>()
        );

        return new PageUtils(page);
    }

}