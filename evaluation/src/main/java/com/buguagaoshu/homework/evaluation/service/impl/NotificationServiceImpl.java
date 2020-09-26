package com.buguagaoshu.homework.evaluation.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.NotificationDao;
import com.buguagaoshu.homework.evaluation.entity.NotificationEntity;
import com.buguagaoshu.homework.evaluation.service.NotificationService;


@Service("notificationService")
public class NotificationServiceImpl extends ServiceImpl<NotificationDao, NotificationEntity> implements NotificationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<NotificationEntity> page = this.page(
                new Query<NotificationEntity>().getPage(params),
                new QueryWrapper<NotificationEntity>()
        );

        return new PageUtils(page);
    }

}