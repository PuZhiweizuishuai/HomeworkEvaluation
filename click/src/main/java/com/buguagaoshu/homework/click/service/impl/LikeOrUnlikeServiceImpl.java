package com.buguagaoshu.homework.click.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.click.dao.LikeOrUnlikeDao;
import com.buguagaoshu.homework.click.entity.LikeOrUnlikeEntity;
import com.buguagaoshu.homework.click.service.LikeOrUnlikeService;


@Service("likeOrUnlikeService")
public class LikeOrUnlikeServiceImpl extends ServiceImpl<LikeOrUnlikeDao, LikeOrUnlikeEntity> implements LikeOrUnlikeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LikeOrUnlikeEntity> page = this.page(
                new Query<LikeOrUnlikeEntity>().getPage(params),
                new QueryWrapper<LikeOrUnlikeEntity>()
        );

        return new PageUtils(page);
    }

}