package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-28 16:04
 * 消息通知
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * TODO 采用 Redis 缓存未读通知
     * */
    @GetMapping("/notification/list")
    public ResponseDetails notification(@RequestParam Map<String, Object> params,
                                        HttpServletRequest request) {
        return ResponseDetails.ok().put("data", notificationService.queryPage(params, request));
    }


    @GetMapping("/notification/read")
    public ResponseDetails read(@RequestParam("id") Long id,
                                HttpServletRequest request) {
        boolean read = notificationService.read(id, request);
        if (read) {
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
    }


    @PostMapping("/notification/readAll")
    public ResponseDetails readAll(HttpServletRequest request) {
        notificationService.readAll(request);
        return ResponseDetails.ok();
    }
}
