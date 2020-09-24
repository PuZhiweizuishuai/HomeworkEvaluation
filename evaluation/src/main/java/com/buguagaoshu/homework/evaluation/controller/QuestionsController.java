package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
import com.buguagaoshu.homework.evaluation.service.QuestionsService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-17 20:14
 */
@RestController
public class QuestionsController {
    private final QuestionsService questionsService;

    @Autowired
    public QuestionsController(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    /**
     * TODO 此接口仅限 教师与管理员访问
     */
    @GetMapping("/questions/list")
    public ResponseDetails findQuestions(@RequestParam Map<String, Object> params,
                                         HttpServletRequest request) {
        return ResponseDetails.ok().put("data",
                questionsService.queryPage(params,
                        JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY)));
    }


    /**
     * 保存问题
     */
    @PostMapping("/question/save")
    public ResponseDetails save(@RequestBody QuestionsModel questionsModel,
                                HttpServletRequest request) {
        if (questionsService.saveQuestion(questionsModel, request)) {
            return ResponseDetails.ok(ReturnCodeEnum.SUCCESS);
        }
        return ResponseDetails.ok(0, "数据有误或没有权限！");
    }


    /**
     * 更新数据
     */
    @PostMapping("/question/update")
    public ResponseDetails update(@RequestBody QuestionsModel questionsModel,
                                  HttpServletRequest request) {
        if (questionsService.updateQuestion(questionsModel, request)) {
            return ResponseDetails.ok(ReturnCodeEnum.SUCCESS);
        }
        return ResponseDetails.ok(0, "提交的更新数据有误或没有权限，请稍后再试！");
    }
}
