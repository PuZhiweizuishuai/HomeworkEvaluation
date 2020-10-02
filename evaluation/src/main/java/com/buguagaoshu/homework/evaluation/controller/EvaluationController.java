package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.model.CommentModel;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.service.EvaluationService;
import com.buguagaoshu.homework.evaluation.vo.EvaluationCommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-01 14:48
 * 作业互评接口
 */
@RestController
public class EvaluationController {
    private final EvaluationService evaluationService;

    @Autowired
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }


    /**
     * 返回需要互评的作业列表
     */
    @GetMapping("/evaluation/homework/{id}")
    public ResponseDetails list(@PathVariable("id") Long courseId,
                                Map<String, Object> params,
                                HttpServletRequest request) {
        PageUtils list = evaluationService.list(courseId, params, request);
        if (list == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", list);
    }


    @GetMapping("/evaluation/list/{id}")
    public ResponseDetails submitList(@PathVariable("id") Long homeworkId,
                                      Map<String, Object> params,
                                      HttpServletRequest request) {
        PageUtils list = evaluationService.userSubmitList(homeworkId, params, request);
        if (list == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", list);
    }


    @GetMapping("/evaluation/submit/{id}")
    public ResponseDetails userSubmitHomework(@PathVariable("id") Long submitId,
                                              HttpServletRequest request) {
        HomeworkModel homeworkModel = evaluationService.userSubmitInfo(submitId, request);
        if (homeworkModel == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
        }
        return ResponseDetails.ok().put("data", homeworkModel);
    }

    @PostMapping("/evaluation/comment/submit")
    public ResponseDetails submitComment(@RequestBody EvaluationCommentVo evaluationCommentVo,
                                         HttpServletRequest request) {
        CommentModel commentModel = evaluationService.saveComment(evaluationCommentVo, request);
        if (commentModel == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND.getCode(), "你没有提交这次作业，所以无法参与此次互评！");
        }
        return ResponseDetails.ok().put("data", commentModel);
    }


    @GetMapping("/evaluation/comment/list/{submitId}")
    public ResponseDetails commentList(@PathVariable("submitId") Long submitId,
                                       Map<String, Object> params,
                                       HttpServletRequest request) {
        PageUtils pageUtils = evaluationService.commentPage(submitId, params, request);
        if (pageUtils == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
        }
        return ResponseDetails.ok().put("data", pageUtils);
    }


    @GetMapping("/evaluation/comment/second/{commentId}")
    public ResponseDetails secondList(@PathVariable("commentId") Long commentId,
                                      Map<String, Object> params,
                                      HttpServletRequest request) {
        PageUtils pageUtils = evaluationService.secondPage(commentId, params, request);
        if (pageUtils == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
        }
        return ResponseDetails.ok().put("data", pageUtils);
    }
}
