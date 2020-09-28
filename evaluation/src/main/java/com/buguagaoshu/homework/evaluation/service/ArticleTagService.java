package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.ArticleTagEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-28 20:50:50
 */
public interface ArticleTagService extends IService<ArticleTagEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 获取分类树
     * @return 分类数
     * */
    List<ArticleTagEntity> listTree();


    Map<Integer,ArticleTagEntity> listToMap();
}

