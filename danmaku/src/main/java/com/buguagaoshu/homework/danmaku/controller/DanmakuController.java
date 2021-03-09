package com.buguagaoshu.homework.danmaku.controller;


import com.alibaba.nacos.common.http.HttpUtils;
import com.buguagaoshu.homework.common.domain.DanmakuDetails;
import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.danmaku.feign.DanmakuFeignService;
import com.buguagaoshu.homework.danmaku.service.DanmakuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-07 16:02
 */
@RestController
@RefreshScope
public class DanmakuController {

    private final DanmakuService danmakuService;

    private final DanmakuFeignService danmakuFeignService;

    @Value("${JWT.CookieToken}")
    public static String COOKIE_TOKEN = "COOKIE-TOKEN";

    @Autowired
    public DanmakuController(DanmakuService danmakuService, DanmakuFeignService danmakuFeignService) {
        this.danmakuService = danmakuService;
        this.danmakuFeignService = danmakuFeignService;
    }

    @GetMapping("/danmaku/v3")
    public ResponseDetails get(@RequestParam("id") Long id,
                               @RequestParam("max") Integer max) {
        return ResponseDetails.ok().put("data", danmakuService.danmakuList(id)).put("code", 0);
    }


    @PostMapping("/danmaku/v3")
    public ResponseDetails post(@RequestBody DanmakuDetails danmakuVo,
                                HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_TOKEN);
        if (cookie == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND).put("code", 1);
        }
        danmakuVo.setToken(cookie.getValue());
        return danmakuFeignService.saveCheck(danmakuVo);
    }
}
