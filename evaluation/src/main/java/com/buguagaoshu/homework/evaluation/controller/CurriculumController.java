package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.CurriculumEntity;
import com.buguagaoshu.homework.evaluation.model.CurriculumModel;
import com.buguagaoshu.homework.evaluation.model.JoinCourseCode;
import com.buguagaoshu.homework.evaluation.service.CurriculumService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import com.buguagaoshu.homework.evaluation.vo.CurriculumInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-07 16:52
 * 课程接口
 */
@RestController
@RequestMapping("/api")
public class CurriculumController {

    private final CurriculumService curriculumService;

    @Autowired
    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }


    /**
     * 教师创建新的课程
     * */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/teacher/curriculum/create")
    public ResponseDetails createCurriculum(@Valid @RequestBody CurriculumModel curriculumModel,
                                            HttpServletRequest request) {
        CurriculumEntity curriculumEntity = curriculumService.createCurriculum(curriculumModel,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY));
        return ResponseDetails.ok().put("data", curriculumEntity);
    }

    /**
     * 更新课程信息
     * */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/teacher/curriculum/update")
    public ResponseDetails update(@Valid @RequestBody CurriculumModel curriculumModel,
                                  HttpServletRequest request) {
        CurriculumEntity curriculumEntity = curriculumService.updateCurriculum(curriculumModel, request);
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
        return ResponseDetails.ok().put("data", pageUtils);
    }


    /**
     * 课程信息
     * */
    @GetMapping("/curriculum/info/{id}")
    public ResponseDetails info(@PathVariable("id") Long id) {
        CurriculumInfo entity = curriculumService.info(id);
        if (entity != null) {
            return ResponseDetails.ok().put("data", entity);
        }
        return ResponseDetails.ok(ReturnCodeEnum.NOO_FOUND);
    }

    /**
     * 加入课程
     * */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @PostMapping("/curriculum/join/{id}")
    public ResponseDetails join(@PathVariable("id") Long id,
                                @RequestBody JoinCourseCode code,
                                HttpServletRequest request) {
        return ResponseDetails.ok(curriculumService.join(id, request, code));
    }



    /**
     * 返回课程学习页需要的信息
     * */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @GetMapping("/curriculum/learn/{id}")
    public ResponseDetails learn(@PathVariable("id") Long id,
                                     HttpServletRequest request) {
        Map<String, Object> map = curriculumService.learn(id, request);
        if (map != null) {
            return ResponseDetails.ok().put("data", map);
        }
        return ResponseDetails.ok(ReturnCodeEnum.NOT_SELECT_THIS_COURSE);
    }
}
