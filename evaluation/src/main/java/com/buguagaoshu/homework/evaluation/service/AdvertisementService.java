package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.AdvertisementEntity;
import com.buguagaoshu.homework.evaluation.model.AdvertisementModel;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-10 16:31:59
 */
public interface AdvertisementService extends IService<AdvertisementEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 添加广告或者首页大图
     * @param advertisementModel 新的广告或者首页大图
     * @param nowLoginUser 当前操作的用户
     * @return 加入成功的广告
     * */
    AdvertisementEntity add(AdvertisementModel advertisementModel, String nowLoginUser);

    /**
     * 更新广告数据
     * @param model  广告
     * @param nowLoginUser 当前操作的用户
     * @return 更新结果
     * */
    AdvertisementEntity updateAd(AdvertisementModel model, String nowLoginUser);


    /**
     * 停止正在投放的广告
     * @param id 广告ID
     * @return 结果
     * */
    ReturnCodeEnum stop(Long id);

    /**
     * 查询运行中的广告
     * 并添加
     * */
    void addAdCache();
}

