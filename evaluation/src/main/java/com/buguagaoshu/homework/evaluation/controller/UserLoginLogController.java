package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.service.UserLoginLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-30 21:50
 * 获取登录历史
 */
@RestController
public class UserLoginLogController {
    private final UserLoginLogService loginLogService;

    public UserLoginLogController(UserLoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }


    @GetMapping("/login/log/list")
    public ResponseDetails list(@RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        return ResponseDetails.ok().put("data", loginLogService.queryPage(params, request));
    }
}
