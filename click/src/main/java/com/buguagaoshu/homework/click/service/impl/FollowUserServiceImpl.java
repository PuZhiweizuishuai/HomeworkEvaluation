package com.buguagaoshu.homework.click.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.click.dao.FollowUserDao;
import com.buguagaoshu.homework.click.entity.FollowUserEntity;
import com.buguagaoshu.homework.click.service.FollowUserService;


@Service("followUserService")
public class FollowUserServiceImpl extends ServiceImpl<FollowUserDao, FollowUserEntity> implements FollowUserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<FollowUserEntity> page = this.page(
                new Query<FollowUserEntity>().getPage(params),
                new QueryWrapper<FollowUserEntity>()
        );

        return new PageUtils(page);
    }

}