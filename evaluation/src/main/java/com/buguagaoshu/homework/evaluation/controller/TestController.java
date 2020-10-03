package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-03 22:17
 */
@RestController
public class TestController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test/admin")
    public ResponseDetails test() {
        return ResponseDetails.ok().put("data", "只有管理员可以访问！");
    }


    @GetMapping("/test/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseDetails teacher() {
        return ResponseDetails.ok().put("data", "只有老师可访问！");
    }

    @GetMapping("/test/teacherAndAdmin")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseDetails teacherAndAdmin() {
        return ResponseDetails.ok().put("data", "教师和管理员都可以访问！");
    }

    @GetMapping("/test/login")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    public ResponseDetails user() {
        return ResponseDetails.ok().put("data", "注册就可访问！");
    }


    @GetMapping("/test/all")
    @PreAuthorize("permitAll()")
    public ResponseDetails all() {
        return ResponseDetails.ok().put("data", "所有人！");
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/test/post")
    public ResponseDetails post() {
        return ResponseDetails.ok().put("data", "post");
    }
}
