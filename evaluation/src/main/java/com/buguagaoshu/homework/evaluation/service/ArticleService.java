package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.model.ArticleModel;
import com.buguagaoshu.homework.evaluation.vo.ArticleVo;
import com.buguagaoshu.homework.evaluation.vo.DeleteVo;
import com.buguagaoshu.homework.evaluation.vo.ThinkVo;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
     * @param articleVo 帖子内容
     * @param request 用户信息
     * @return 保存结果
     * */
    ArticleEntity saveArticle(ArticleVo articleVo, HttpServletRequest request);


    /**
     * 获取当前用户对本课程评价
     * */
    ArticleEntity getCourseRating(Long courseId, HttpServletRequest request);


    ArticleEntity getCourseRating(Long courseId, Claims user);


    /**
     * 获取当前课程下的主题帖
     * @param courseId 课程ID
     * @param request 获取请求用户信息
     * @param params 请求参数
     *               page: 页码
     *               limit： 每页显示数量
     *               sort： 0 发帖时间倒序
     *                     1  回复时间
     *                     2  精品
     *                     3 消灭零回复
     *               key： 搜索参数
     * @return 分页后的结果
     * */
    PageUtils getCourseList(Long courseId, Map<String, Object> params, HttpServletRequest request);

    /**
     * 获取课程内的讨论贴
     * @param id 帖子ID
     * @param request 用户数据
     * @return 帖子数据
     * */
    ArticleModel courseArticleInfo(Long id, HttpServletRequest request);

    /**
     * 阅读数，评论数，喜欢数，关注数增加
     * */
    void countNumberCount(String col, Long articleId, Integer count);

    /**
     * 删除帖子
     * */
    boolean deleteArticle(DeleteVo deleteVo, HttpServletRequest request);

    /**
     * 加精与取消加精
     * 如果帖子是加精的，那么执行会取消加精
     * 如果不是，那么会变成加精贴
     * */
    boolean perfect(DeleteVo deleteVo, HttpServletRequest request);


    /**
     * 返回课程评价
     *
     * */
    PageUtils courseRating(Long courseId, Map<String, Object> params);

    /**
     * 更新课程评价
     * */
    ArticleEntity updateUserCourseRating(ArticleVo articleVo, HttpServletRequest request);


    /**
     * 返回当前帖子列表
     * */
    PageUtils getArticleList(Map<String, Object> params, HttpServletRequest request);



    ArticleModel getArticleInfo(Long id, HttpServletRequest request);


    /**
     * 保存想法
     * */
    ArticleEntity saveThink(ThinkVo thinkVo, HttpServletRequest request);

    /**
     * 获取想法列表
     * */
    PageUtils getThinkList(Map<String, Object> params, HttpServletRequest request);
}

