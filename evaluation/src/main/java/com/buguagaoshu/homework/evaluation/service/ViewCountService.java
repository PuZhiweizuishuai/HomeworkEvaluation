package com.buguagaoshu.homework.evaluation.service;

import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-12 16:18
 * 处理帖子浏览量统计
 */
public interface ViewCountService {
    /**
     * 浏览量增加
     * 使用IP进行判断，同一IP两小时内访问只算一次浏览
     * @param request 获取请求IP
     * @param articleId 帖子ID
     * */
    void viewCountAdd(HttpServletRequest request, Long articleId);


    /**
     * 浏览量增加
     * 批量增加浏览量
     * 使用IP进行判断，同一IP两小时内访问只算一次浏览
     * @param request 获取请求IP
     * @param articleIds 帖子ID列表
     * */
    void viewCountAdd(HttpServletRequest request, List<ArticleEntity> articleIds);

    /**
     * 获取浏览量
     * @param articleId 帖子ID
     * @return 浏览量，默认为 0
     * */
    long getViewCount(Long articleId);

    Map<Long, Long> multiGetViewCount(List<ArticleEntity> ids);


    /**
     * 执行同步操作，将Redis中数据同步到 MySQL 中
     * */
    void synchronizedViewData();
}
