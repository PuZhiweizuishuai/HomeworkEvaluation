package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.cache.WebsiteIndexMessageCache;
import com.buguagaoshu.homework.evaluation.model.AdvertisementModel;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.AdvertisementDao;
import com.buguagaoshu.homework.evaluation.entity.AdvertisementEntity;
import com.buguagaoshu.homework.evaluation.service.AdvertisementService;
import org.springframework.util.StringUtils;


@Service("advertisementService")
public class AdvertisementServiceImpl extends ServiceImpl<AdvertisementDao, AdvertisementEntity> implements AdvertisementService {

    private final WebsiteIndexMessageCache websiteIndexMessageCache;

    private final long DAY = 86400000;

    @Autowired
    public AdvertisementServiceImpl(WebsiteIndexMessageCache websiteIndexMessageCache) {
        this.websiteIndexMessageCache = websiteIndexMessageCache;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<AdvertisementEntity> wrapper = new QueryWrapper<>();

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            long time = System.currentTimeMillis();
            if ("1".equals(status)) {
                wrapper.le("start_time", time);
                wrapper.ge("end_time", time);
            } else {
                wrapper.ge("start_time", time).or().le("end_time", time);
            }
        }

        String type = (String) params.get("type");
        if (!StringUtils.isEmpty(type) && !"-1".equals(type)) {
            wrapper.eq("type", type);
        }
        String title = (String) params.get("title");
        if (!StringUtils.isEmpty(title)) {
            wrapper.like("title", title);
        }
        wrapper.orderByDesc("create_time");
        IPage<AdvertisementEntity> page = this.page(
                new Query<AdvertisementEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public AdvertisementEntity add(AdvertisementModel advertisementModel, String nowLoginUser) {
        AdvertisementEntity ad = new AdvertisementEntity();
        ad.setCreateTime(System.currentTimeMillis());
        ad.setUpdateTime(System.currentTimeMillis());
        ad.setCreateUser(nowLoginUser);
        BeanUtils.copyProperties(advertisementModel, ad);
        ad.setId(IdWorker.getId());
        ad.setStartTime(TimeUtils.parseTime(advertisementModel.getStartTime()));
        ad.setEndTime(TimeUtils.parseTime(advertisementModel.getEndTime()));
        this.save(ad);
        // 加入缓存
        websiteIndexMessageCache.addCache(ad);
        return ad;
    }

    @Override
    public AdvertisementEntity updateAd(AdvertisementModel model, String nowLoginUser) {
        if (model.getId() == null) {
            return null;
        }
        AdvertisementEntity advertisementEntity = this.getById(model.getId());
        BeanUtils.copyProperties(model, advertisementEntity);
        Long startTime = advertisementEntity.getStartTime();
        Long endTime = advertisementEntity.getEndTime();
        // 开始时间特殊处理
        try {
            startTime = TimeUtils.parseTime(model.getStartTime());
        } catch (RuntimeException ignored) {
        }

        //
        advertisementEntity.setStartTime(startTime);
        if (model.getEndTime() != null) {
            try {
                endTime = TimeUtils.parseTime(model.getEndTime());
                advertisementEntity.setEndTime(endTime);
            } catch (Exception ignored) {
            }
        } else {
            advertisementEntity.setEndTime(endTime);
        }

        advertisementEntity.setUpdateTime(System.currentTimeMillis());
        this.updateById(advertisementEntity);

        // 处理缓存
        websiteIndexMessageCache.addCache(advertisementEntity);
        return advertisementEntity;
    }

    @Override
    public ReturnCodeEnum stop(Long id) {
        AdvertisementEntity entity = this.getById(id);
        if (entity != null && websiteIndexMessageCache.judgeRun(entity)) {
            entity.setStartTime(0L);
            entity.setEndTime(0L);
            entity.setUpdateTime(System.currentTimeMillis());
            this.updateById(entity);
            // 处理缓存
            websiteIndexMessageCache.remove(id);
            return ReturnCodeEnum.SUCCESS;
        }
        return ReturnCodeEnum.NOT_RUN;
    }

    @Override
    public void addAdCache() {
        QueryWrapper<AdvertisementEntity> wrapper = new QueryWrapper<>();
        long time = System.currentTimeMillis();
        wrapper.le("start_time", time);
        wrapper.ge("end_time", time);
        IPage<AdvertisementEntity> page = this.page(
                new Query<AdvertisementEntity>().getPage(new HashMap<>()),
                wrapper
        );
        List<AdvertisementEntity> list = page.getRecords();

        list.forEach(websiteIndexMessageCache::addCache);
    }
}
