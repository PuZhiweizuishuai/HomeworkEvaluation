package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.entity.BulletinEntity;
import com.buguagaoshu.homework.evaluation.service.BulletinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-17 16:18
 * 发送课程公告
 */
@RestController
public class BulletinController {

    private final BulletinService bulletinService;

    @Autowired
    public BulletinController(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }

    /**
     * 获取当前课程通知列表
     * */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @GetMapping("/bulletin/list/{id}")
    public ResponseDetails list(@PathVariable("id") Long id,
                                @RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        return ResponseDetails.ok().put("data", bulletinService.queryPage(id, params, request));
    }


    /**
     * 发布公告
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT')")
    @PostMapping("/bulletin/post")
    public ResponseDetails post(@Valid @RequestBody BulletinEntity bulletinEntity,
                                HttpServletRequest request) {
        return ResponseDetails.ok(bulletinService.saveBulletin(bulletinEntity, request));
    }

    /**
     * 更新公告
     * */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT')")
    @PostMapping("/bulletin/update")
    public ResponseDetails update(@Valid @RequestBody BulletinEntity bulletinEntity,
                                  HttpServletRequest request) {
        return ResponseDetails.ok(bulletinService.updateBulletin(bulletinEntity, request));
    }
}
