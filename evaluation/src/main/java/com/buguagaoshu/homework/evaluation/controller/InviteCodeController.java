package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.InviteCodeEntity;
import com.buguagaoshu.homework.evaluation.service.InviteCodeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-02 22:38
 */
@RestController
public class InviteCodeController {
    private final InviteCodeService inviteCodeService;

    public InviteCodeController(InviteCodeService inviteCodeService) {
        this.inviteCodeService = inviteCodeService;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/invite/code/create")
    public ResponseDetails create(@RequestBody InviteCodeEntity inviteCodeEntity,
                                  HttpServletRequest request) {
        InviteCodeEntity code = inviteCodeService.create(inviteCodeEntity, request);
        if (code == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", code);
    }


    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/invite/code/list")
    public ResponseDetails list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        PageUtils pageUtils = inviteCodeService.pageList(params, request);
        if (pageUtils == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", pageUtils);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/invite/code/log")
    public ResponseDetails log(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        PageUtils pageUtils = inviteCodeService.useLog(params, request);
        if (pageUtils == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", pageUtils);
    }
}
