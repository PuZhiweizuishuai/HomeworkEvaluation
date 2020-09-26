package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitHomeworkStatusEntity;
import com.buguagaoshu.homework.evaluation.model.HomeworkAnswer;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.service.HomeworkService;
import com.buguagaoshu.homework.evaluation.service.SubmitHomeworkStatusService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.TeacherCommentHomeworkData;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-12 17:13
 */
@RestController
public class HomeworkController {
    private final HomeworkService homeworkService;

    private final SubmitHomeworkStatusService submitHomeworkStatusService;

    @Autowired
    public HomeworkController(HomeworkService homeworkService, SubmitHomeworkStatusService submitHomeworkStatusService) {
        this.homeworkService = homeworkService;
        this.submitHomeworkStatusService = submitHomeworkStatusService;
    }

    /**
     * 添加作业，教师和管理员可访问
     */
    @PostMapping("/homework/save")
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
    @GetMapping("/homework/list/{id}")
    public ResponseDetails list(@PathVariable("id") Long courseId,
                                @RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        // TODO 返回用户作业提交信息
        PageUtils pageUtils = homeworkService.courseHomeworkList(courseId, params, request);
        if (pageUtils == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", pageUtils);
    }

    /**
     * 获取当前作业问题列表
     */
    @GetMapping("/homework/info/{id}")
    public ResponseDetails homeworkQuestionList(@PathVariable("id") Long homeworkId,
                                                HttpServletRequest request) throws JsonProcessingException {
        HomeworkModel homeworkModel = homeworkService.courseQuestionList(homeworkId,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY), false);
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


    /**
     * 获取学生提交
     */
    @GetMapping("/homework/keeper/correct/{id}")
    public ResponseDetails teacherGetStudentSubmit(@PathVariable("id") Long homeworkId,
                                                   @RequestParam("studentId") String studentId,
                                                   HttpServletRequest request) throws JsonProcessingException {
        Claims teacher = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        HomeworkModel homeworkModel = homeworkService.teacherGetStudentAnswer(homeworkId, studentId, teacher.getId());
        if (homeworkModel == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER.getCode(), "作业不存在或没有权限或当前学生还没有提交任何数据!");
        }
        return ResponseDetails.ok().put("data", homeworkModel);
    }


    /**
     * 更新作业设置
     */
    @PostMapping("/homework/setting/update")
    public ResponseDetails homeworkUpdate(@RequestBody HomeworkModel homeworkModel,
                                          HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        return ResponseDetails.ok(homeworkService.updateHomework(homeworkModel, user));
    }


    /**
     * 批改作业接口
     */
    @PostMapping("/homework/keeper/correct")
    public ResponseDetails correct(@Valid @RequestBody TeacherCommentHomeworkData teacherCommentHomeworkData,
                                   HttpServletRequest request) {
        ReturnCodeEnum returnCodeEnum = homeworkService.teacherCorrect(teacherCommentHomeworkData, request);
        if (returnCodeEnum.equals(ReturnCodeEnum.SUCCESS)) {
            List<SubmitHomeworkStatusEntity> list = submitHomeworkStatusService.teacherNoCommentSubmit(teacherCommentHomeworkData.getId());
            return ResponseDetails.ok().put("data", list);
        }
        return ResponseDetails.ok();
    }


}
