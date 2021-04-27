package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.entity.LikeOrUnlikeEntity;
import com.buguagaoshu.homework.evaluation.service.LikeOrUnlikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-27 14:13
 */
@RestController
public class LikeOrUnLikeController {
    private final LikeOrUnlikeService likeOrUnlikeService;

    @Autowired
    public LikeOrUnLikeController(LikeOrUnlikeService likeOrUnlikeService) {
        this.likeOrUnlikeService = likeOrUnlikeService;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @PostMapping("/click/like")
    public ResponseDetails like(@Valid @RequestBody LikeOrUnlikeEntity likeOrUnlikeEntity,
                                HttpServletRequest request) {
        if (likeOrUnlikeService.clickLick(likeOrUnlikeEntity, request)) {
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(ReturnCodeEnum.NOO_FOUND);
    }
}
