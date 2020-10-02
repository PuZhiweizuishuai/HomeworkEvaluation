package com.buguagaoshu.homework.evaluation.service;

import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.model.CommentModel;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.vo.EvaluationCommentVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-01 14:48
 * 互评接口
 */
public interface EvaluationService {
    /**
     * 返回需要互评的作业列表
     *
     * @param courseId 课程ID
     * @param params   查询参数
     * @param request  请求用户
     * @return 列表
     */
    PageUtils list(Long courseId, Map<String, Object> params, HttpServletRequest request);


    /**
     * 返回当前作业提交的用户列表列表
     *
     * @param homeworkId 作业ID
     * @param params     查询参数
     * @param request    请求用户
     * @return 列表
     */
    PageUtils userSubmitList(Long homeworkId, Map<String, Object> params, HttpServletRequest request);

    /**
     * 返回用户提交的作业信息
     *
     * @param submitId 提交作业的ID
     * @return 用户提交的作业内容
     */
    HomeworkModel userSubmitInfo(Long submitId, HttpServletRequest request);

    /**
     * 返回当前作业的评价信息
     */
    PageUtils commentPage(Long submitId, Map<String, Object> params, HttpServletRequest request);


    PageUtils secondPage(Long commentId, Map<String, Object> params, HttpServletRequest request);

    /**
     * 提交当前作业的评价信息
     */
    CommentModel saveComment(EvaluationCommentVo evaluationCommentVo, HttpServletRequest request);
}
