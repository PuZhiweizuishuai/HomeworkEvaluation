package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.CommentEntity;
import com.buguagaoshu.homework.evaluation.model.CommentModel;
import com.buguagaoshu.homework.evaluation.model.ReplyComment;
import com.buguagaoshu.homework.evaluation.vo.CommentVo;
import com.buguagaoshu.homework.evaluation.vo.DeleteVo;

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
     *
     * @param commentVo 评论内容
     * @param request   请求用户
     * @return 返回评论数据
     */
    CommentModel saveComment(CommentVo commentVo, HttpServletRequest request);

    /**
     * 返回评论列表
     *
     * @param articleId 目标帖子ID
     * @param params    查询参数
     *                  page： 页码
     *                  size： 每页显示数量
     *                  sort： 排序
     * @param request   获取请求用户
     * @return 结果
     */
    PageUtils commentList(Long articleId, Map<String, Object> params, HttpServletRequest request);

    /**
     * 返回单个评论信息
     *
     * @param commentId 评论ID
     * @param request   请求用户
     * @return 回复数据
     */
    ReplyComment replyComment(Long commentId, HttpServletRequest request);


    /**
     * 返回二级评论列表
     *
     * @param id      评论ID
     * @param request 请求的用户
     * @param params  请求参数
     * @return 结果
     */
    PageUtils secondCommentList(Long id, Map<String, Object> params, HttpServletRequest request);


    /**
     * 删除评论
     *
     * @param deleteVo 要删除的评论ID
     * @param request   请求用户
     * @return 删除结果
     */
    boolean deleteComment(DeleteVo deleteVo, HttpServletRequest request);
}

