package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 帖子表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-28 20:13:56
 */
public interface ArticleService extends IService<ArticleEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存新的帖子
     * @param articleEntity 帖子内容
     * @param request 用户信息
     * @return 保存结果
     * */
    ArticleEntity saveArticle(ArticleEntity articleEntity, HttpServletRequest request);


    /**
     * 获取当前课程下的主题帖
     * @param courseId 课程ID
     * @param request 获取请求用户信息
     * @param params 请求参数
     *               page: 页码
     *               limit： 每页显示数量
     *               sort： 0 发帖时间倒序
     *                     1  回复时间
     *                     2  热门
     *               key： 搜索参数
     * @return 分页后的结果
     * */
    PageUtils getCourseList(Long courseId, Map<String, Object> params, HttpServletRequest request);
}

