package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
import com.buguagaoshu.homework.evaluation.service.HomeworkService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public ResponseDetails add(@Validated @RequestBody HomeworkModel homeworkModel,
                               HttpServletRequest request) {
        System.out.println(homeworkModel);

        HomeworkModel homework = homeworkService.add(homeworkModel,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY));
        if (homework != null) {
            return ResponseDetails.ok().put("data", homework);
        }
        return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
    }

    @GetMapping("/homework/list/{id}")
    public ResponseDetails list(@PathVariable("id") Long courseId,
                                HttpServletRequest request) {
        List<HomeworkEntity> list = homeworkService.courseHomeworkList(courseId,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId());
        if (list == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", list);
    }

    @GetMapping("/homework/question/{id}")
    public ResponseDetails homeworkQuestionList(@PathVariable("id") Long homeworkId,
                                                HttpServletRequest request) {
        List<QuestionsModel> list = homeworkService.courseQuestionList(homeworkId,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId());
        if (list == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", list);
    }
}
