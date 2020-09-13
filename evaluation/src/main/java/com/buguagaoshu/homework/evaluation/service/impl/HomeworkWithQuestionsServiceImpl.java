package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.buguagaoshu.homework.common.utils.Query;
import com.buguagaoshu.homework.evaluation.dao.HomeworkWithQuestionsDao;
import com.buguagaoshu.homework.evaluation.entity.HomeworkWithQuestionsEntity;
import com.buguagaoshu.homework.evaluation.service.HomeworkWithQuestionsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;



/**
 * @author Pu Zhiwei
 * */
@Service("homeworkWithQuestuionService")
public class HomeworkWithQuestionsServiceImpl extends ServiceImpl<HomeworkWithQuestionsDao, HomeworkWithQuestionsEntity> implements HomeworkWithQuestionsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HomeworkWithQuestionsEntity> page = this.page(
                new Query<HomeworkWithQuestionsEntity>().getPage(params),
                new QueryWrapper<HomeworkWithQuestionsEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Map<Long, HomeworkWithQuestionsEntity> homeworkWithQuestionMap(long homeworkId) {
        List<HomeworkWithQuestionsEntity> homeworkWithQuestionsEntities
                = this
                .list(new QueryWrapper<HomeworkWithQuestionsEntity>().eq("homework_id", homeworkId));
        if (homeworkWithQuestionsEntities == null) {
            return null;
        }
        Map<Long, HomeworkWithQuestionsEntity> questionsMaps =
                homeworkWithQuestionsEntities
                        .stream().collect(Collectors.toMap(HomeworkWithQuestionsEntity::getQuestionId, h -> h));
        return questionsMaps;
    }
}