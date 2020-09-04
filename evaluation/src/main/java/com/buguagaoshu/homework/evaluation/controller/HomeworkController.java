package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.model.HomeworkAnswer;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.service.HomeworkService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
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
     */
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


    /**
     * 获取当前课程作业列表
     */
    @GetMapping("/homework/info/{id}")
    public ResponseDetails list(@PathVariable("id") Long courseId,
                                HttpServletRequest request) {
        // TODO 返回用户作业提交信息
        List<HomeworkEntity> list = homeworkService.courseHomeworkList(courseId,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId());
        if (list == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", list);
    }

    /**
     * 获取当前作业问题列表
     */
    @GetMapping("/homework/question/{id}")
    public ResponseDetails homeworkQuestionList(@PathVariable("id") Long homeworkId,
                                                HttpServletRequest request) throws JsonProcessingException {
        HomeworkModel homeworkModel = homeworkService.courseQuestionList(homeworkId,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId());
        if (homeworkModel == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER.getCode(), "没有获取题目的权力，或者是你已经迟到太久，超过了最迟可进入的时间");
        }
        return ResponseDetails.ok().put("data", homeworkModel);
    }


    /**
     * 作业提交接口
     */
    @PostMapping("/homework/submit")
    public ResponseDetails submitUserHomework(@Validated @RequestBody HomeworkAnswer homeworkAnswer,
                                              HttpServletRequest request) throws JsonProcessingException {
        ReturnCodeEnum returnCodeEnum = homeworkService.submitUserHomework(homeworkAnswer,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY));
        return ResponseDetails.ok(returnCodeEnum);
    }


    /**
     * 获取有无批改作业的权限
     */
    @GetMapping("/homework/keeper/info/{id}")
    public ResponseDetails checkSettingPower(@PathVariable("id") Long homeworkId,
                                             HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);

        return ResponseDetails.ok().put("data", homeworkService.keeperInfo(homeworkId, user));
    }


    @PostMapping("/homework/setting/update")
    public ResponseDetails homeworkUpdate(@RequestBody HomeworkEntity homeworkEntity,
                                          HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);

        return ResponseDetails.ok(homeworkService.updateHomework(homeworkEntity, user));
    }


}
