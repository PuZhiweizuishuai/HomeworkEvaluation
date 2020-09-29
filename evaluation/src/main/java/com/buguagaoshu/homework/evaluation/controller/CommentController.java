package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.model.CommentModel;
import com.buguagaoshu.homework.evaluation.service.CommentService;
import com.buguagaoshu.homework.evaluation.vo.CommentVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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


    @PostMapping("/comment/save")
    public ResponseDetails save(@RequestBody CommentVo commentVo,
                                HttpServletRequest request) {
        CommentModel commentModel = commentService.saveComment(commentVo, request);
        if (commentModel == null) {
            return ResponseDetails.ok(0, "所评论的帖子可能被删除被锁定或没有评论权限！");
        }
        return ResponseDetails.ok().put("data", commentModel);
    }

    @GetMapping("/comment/list/{id}")
    public ResponseDetails list(@PathVariable("id") Long articleId,
                                @RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        PageUtils page = commentService.commentList(articleId, params, request);
        if (page == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
        }
        return ResponseDetails.ok().put("data", page);
    }
}
