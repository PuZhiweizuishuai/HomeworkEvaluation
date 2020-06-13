package com.buguagaoshu.homework.evaluation.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.service.HomeworkService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-12 17:13
 */
@RestController
public class HomeworkController {
    private final HomeworkService homeworkService;

    @Autowired
    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    /**
     * 添加作业，教师和管理员可访问
     * */
    @PostMapping("/homework/add")
    public ResponseDetails add(@RequestBody HomeworkEntity homeworkEntity,
                                HttpServletRequest request) {
        HomeworkEntity homework = homeworkService.add(homeworkEntity,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY));
        return ResponseDetails.ok();
    }
}
