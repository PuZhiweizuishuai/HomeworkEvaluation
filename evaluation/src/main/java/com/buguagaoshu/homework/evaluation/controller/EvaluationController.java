package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-01 14:48
 * 作业互评接口
 * TODO 没有提交，无法评价其他人作业
 */
@RestController
public class EvaluationController {
    private final EvaluationService evaluationService;

    @Autowired
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }


    /**
     * 返回需要互评的作业列表
     */
    @GetMapping("/evaluation/homework/{id}")
    public ResponseDetails list(@PathVariable("id") Long courseId,
                                Map<String, Object> params,
                                HttpServletRequest request) {
        PageUtils list = evaluationService.list(courseId, params, request);
        if (list == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", list);
    }


    @GetMapping("/evaluation/list/{id}")
    public ResponseDetails submitList(@PathVariable("id") Long homeworkId,
                                      Map<String, Object> params,
                                      HttpServletRequest request) {
        PageUtils list = evaluationService.userSubmitList(homeworkId, params, request);
        if (list == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", list);
    }


    @GetMapping("/evaluation/submit/{id}")
    public ResponseDetails userSubmitHomework(@PathVariable("id") Long submitId,
                                              HttpServletRequest request) {
        HomeworkModel homeworkModel = evaluationService.userSubmitInfo(submitId, request);
        if (homeworkModel == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
        }
        return ResponseDetails.ok().put("data", homeworkModel);
    }
}
