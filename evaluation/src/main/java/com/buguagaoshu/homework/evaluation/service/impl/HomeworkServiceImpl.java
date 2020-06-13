package com.buguagaoshu.homework.evaluation.service.impl;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.HomeworkDao;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.service.HomeworkService;


/**
 * @author puzhiwei
 */
@Service("homeworkService")
public class HomeworkServiceImpl extends ServiceImpl<HomeworkDao, HomeworkEntity> implements HomeworkService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HomeworkEntity> page = this.page(
                new Query<HomeworkEntity>().getPage(params),
                new QueryWrapper<HomeworkEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public HomeworkEntity add(HomeworkEntity homeworkEntity, Claims nowLoginUser) {

        return null;
    }

}
