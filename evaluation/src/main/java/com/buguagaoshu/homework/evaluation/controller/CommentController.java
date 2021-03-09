package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.CommentTypeEnum;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.model.CommentModel;
import com.buguagaoshu.homework.evaluation.model.ReplyComment;
import com.buguagaoshu.homework.evaluation.service.CommentService;
import com.buguagaoshu.homework.evaluation.vo.CommentVo;
import com.buguagaoshu.homework.evaluation.vo.DeleteVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-29 21:12
 */
@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @PostMapping("/comment/save")
    public ResponseDetails save(@Valid @RequestBody CommentVo commentVo,
                                HttpServletRequest request) {
        CommentModel commentModel = commentService.saveArticleComment(commentVo, request);
        if (commentModel == null) {
            return ResponseDetails.ok(0, "所评论的帖子可能被删除被锁定或没有评论权限！");
        }
        return ResponseDetails.ok().put("data", commentModel);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @PostMapping("/comment/delete")
    public ResponseDetails delete(@RequestBody DeleteVo deleteVo,
                                  HttpServletRequest request) {
        if(commentService.deleteComment(deleteVo, request)) {
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @GetMapping("/comment/list/{id}")
    public ResponseDetails list(@PathVariable("id") Long articleId,
                                @RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        PageUtils page = commentService.articleCommentList(articleId, params, request, CommentTypeEnum.ORDINARY_ONE_LEVEL_COMMENT);
        if (page == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
        }
        return ResponseDetails.ok().put("data", page);
    }

    /**
     * 返回单个评论信息
     * */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @GetMapping("/comment/reply/{commentId}")
    public ResponseDetails replyComment(@PathVariable("commentId") Long commentId,
                                       HttpServletRequest request) {
        ReplyComment replyComment = commentService.replyComment(commentId, request);
        if (replyComment == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
        }
        return ResponseDetails.ok().put("data", replyComment);
    }

    /**
     * 返回二级评论列表
     * */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @GetMapping("/comment/second/list/{id}")
    public ResponseDetails secondCommentList(@PathVariable("id") Long id,
                                             @RequestParam Map<String, Object> params,
                                             HttpServletRequest request) {
        PageUtils pageUtils = commentService.secondCommentList(id, params, request);
        if (pageUtils == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
        }
        return ResponseDetails.ok().put("data", pageUtils);
    }
}
