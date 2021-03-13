package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.entity.VoteLogEntity;
import com.buguagaoshu.homework.evaluation.service.VoteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-13 23:34
 */
@RestController
public class VoteController {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/vote")
    public ResponseDetails vote(@Valid @RequestBody VoteLogEntity voteLogEntity, HttpServletRequest request) {
        return voteService.vote(voteLogEntity, request);
    }
}
