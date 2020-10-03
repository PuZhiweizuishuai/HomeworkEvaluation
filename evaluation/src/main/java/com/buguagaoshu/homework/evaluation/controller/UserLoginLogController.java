package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.service.UserLoginLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-30 21:50
 * 获取登录历史
 */
@RestController
@RequestMapping("/api")
public class UserLoginLogController {
    private final UserLoginLogService loginLogService;

    public UserLoginLogController(UserLoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }


    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @GetMapping("/login/log/list")
    public ResponseDetails list(@RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        return ResponseDetails.ok().put("data", loginLogService.queryPage(params, request));
    }
}
