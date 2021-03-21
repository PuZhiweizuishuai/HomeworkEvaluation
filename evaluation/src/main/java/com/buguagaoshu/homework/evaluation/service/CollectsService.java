package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.CollectsEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 收藏表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2021-03-17 23:32:57
 */
public interface CollectsService extends IService<CollectsEntity> {

    /**
     * @param params
     *        type 0 默认论坛帖子
     *             1 想法
     *             2 课程
     * */
    PageUtils queryPage(Map<String, Object> params, HttpServletRequest request);

    /**
     * 添加收藏
     * */
    CollectsEntity save(CollectsEntity collectsEntity, HttpServletRequest request);


    /**
     * 查找收藏
     * */
    CollectsEntity findCollectsByUserIdWithArticleId(String userId, Long article);


}

