package com.buguagaoshu.homework.evaluation.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.HistoryDao;
import com.buguagaoshu.homework.evaluation.entity.HistoryEntity;
import com.buguagaoshu.homework.evaluation.service.HistoryService;


@Service("historyService")
public class HistoryServiceImpl extends ServiceImpl<HistoryDao, HistoryEntity> implements HistoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HistoryEntity> page = this.page(
                new Query<HistoryEntity>().getPage(params),
                new QueryWrapper<HistoryEntity>()
        );

        return new PageUtils(page);
    }

}