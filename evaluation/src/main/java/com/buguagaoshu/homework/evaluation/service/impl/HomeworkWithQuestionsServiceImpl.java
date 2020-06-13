package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.buguagaoshu.homework.common.utils.Query;
import com.buguagaoshu.homework.evaluation.dao.HomeworkWithQuestionsDao;
import com.buguagaoshu.homework.evaluation.entity.HomeworkWithQuestionsEntity;
import com.buguagaoshu.homework.evaluation.service.HomeworkWithQuestionsService;
import org.springframework.stereotype.Service;
import java.util.Map;
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
}