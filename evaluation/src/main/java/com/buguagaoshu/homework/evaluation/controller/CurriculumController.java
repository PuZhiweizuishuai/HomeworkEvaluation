package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.CurriculumEntity;
import com.buguagaoshu.homework.evaluation.model.CurriculumModel;
import com.buguagaoshu.homework.evaluation.service.CurriculumService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.CurriculumInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-07 16:52
 * 课程接口
 */
@RestController
public class CurriculumController {

    private final CurriculumService curriculumService;

    @Autowired
    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    /**
     * 教师创建新的课程
     * */
    @PostMapping("/teacher/create/curriculum")
    public ResponseDetails createCurriculum(@RequestBody CurriculumModel curriculumModel,
                                            HttpServletRequest request) {
        CurriculumEntity curriculumEntity = curriculumService.createCurriculum(curriculumModel,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY));
        return ResponseDetails.ok().put("data", curriculumEntity);
    }

    @PostMapping("/teacher/update/curriculum")
    public ResponseDetails update(@RequestBody CurriculumModel curriculumModel,
                                  HttpServletRequest request) {
        CurriculumEntity curriculumEntity = curriculumService.updateCurriculum(curriculumModel,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY));
        if (curriculumEntity == null) {
            return ResponseDetails.ok(404, "没有这个课程或没有权限！");
        }
        return ResponseDetails.ok().put("data", curriculumEntity);
    }

    /**
     * 查找课程列表
     * */
    @GetMapping("/curriculum/list")
    public ResponseDetails list(@RequestParam Map<String, Object> params,
                          HttpServletRequest request) {
        PageUtils pageUtils = curriculumService.selectUserCurriculumList(params, request);
        if (pageUtils == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NOO_FOUND);
        }
        return ResponseDetails.ok().put("page", curriculumService.selectUserCurriculumList(params, request));
    }


    @GetMapping("/curriculum/info/{id}")
    public ResponseDetails info(@PathVariable("id") Long id) {
        CurriculumInfo entity = curriculumService.info(id);
        if (entity != null) {
            return ResponseDetails.ok().put("data", entity);
        }
        return ResponseDetails.ok(ReturnCodeEnum.NOO_FOUND);
    }


    @GetMapping("/curriculum/learn/{id}")
    public ResponseDetails judgeUser(@PathVariable("id") Long id,
                                     HttpServletRequest request) {
        CurriculumEntity curriculumEntity = curriculumService.judgeThisCurriculumUserSelect(id,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId());
        if (curriculumEntity != null) {
            return ResponseDetails.ok().put("data", curriculumEntity);
        }
        return ResponseDetails.ok(ReturnCodeEnum.NOT_SELECT_THIS_COURSE.getCode(),
                ReturnCodeEnum.NOT_SELECT_THIS_COURSE.getMsg());
    }
}
