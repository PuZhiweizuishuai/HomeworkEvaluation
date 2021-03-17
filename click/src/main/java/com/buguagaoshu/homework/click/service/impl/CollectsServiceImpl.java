package com.buguagaoshu.homework.click.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.click.dao.CollectsDao;
import com.buguagaoshu.homework.click.entity.CollectsEntity;
import com.buguagaoshu.homework.click.service.CollectsService;


/**
 * @author Pu Zhiwei
 */
@Service("collectsService")
public class CollectsServiceImpl extends ServiceImpl<CollectsDao, CollectsEntity> implements CollectsService{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CollectsEntity> page = this.page(
                new Query<CollectsEntity>().getPage(params),
                new QueryWrapper<CollectsEntity>()
        );

        return new PageUtils(page);
    }

}