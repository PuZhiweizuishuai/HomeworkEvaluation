package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.entity.CoursewareEntity;
import com.buguagaoshu.homework.evaluation.service.CoursewareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-26 21:40
 * 课件
 */
@RestController
public class CoursewareController {
    private final CoursewareService coursewareService;

    @Autowired
    public CoursewareController(CoursewareService coursewareService) {
        this.coursewareService = coursewareService;
    }


    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT', 'USER')")
    @GetMapping("/course/courseware/{id}")
    public ResponseDetails coursewareTree(@PathVariable("id") Long courseId,
                                          HttpServletRequest request) {

        /**
         * TODO 添加课件缓存，先从缓存中读取课件数据，如果缓存没有，再从数据库中获取数据，之后更新缓存
         * */
        List<CoursewareEntity> tree = coursewareService.coursewareTree(courseId, request);
        if (tree == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER.getCode(), "没有找到这门课程或者你没有加入这门课程！");
        }
        return ResponseDetails.ok().put("data", tree);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT')")
    @PostMapping("/course/courseware/save")
    public ResponseDetails save(@Valid @RequestBody CoursewareEntity coursewareEntity,
                                HttpServletRequest request) {
        boolean result = coursewareService.saveCourseware(coursewareEntity, request);
        if (result) {
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
    }
}
