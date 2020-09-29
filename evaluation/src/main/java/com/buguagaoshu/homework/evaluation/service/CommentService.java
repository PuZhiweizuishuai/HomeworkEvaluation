package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.CommentEntity;
import com.buguagaoshu.homework.evaluation.model.CommentModel;
import com.buguagaoshu.homework.evaluation.vo.CommentVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 评论
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-28 20:13:56
 */
public interface CommentService extends IService<CommentEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存评论
     * @param commentVo 评论内容
     * @param request 请求用户
     * @return 返回评论数据
     * */
    CommentModel saveComment(CommentVo commentVo, HttpServletRequest request);

    /**
     * 返回评论列表
     * @param articleId 目标帖子ID
     * @param params 查询参数
     *               page： 页码
     *               size： 每页显示数量
     *               sort： 排序
     * @param request 获取请求用户
     * @return 结果
     * */
    PageUtils commentList(Long articleId, Map<String, Object> params, HttpServletRequest request);
}

