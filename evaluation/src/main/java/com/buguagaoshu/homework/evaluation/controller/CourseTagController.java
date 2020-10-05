package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.cache.CourseTagCache;
import com.buguagaoshu.homework.evaluation.entity.CourseTagEntity;
import com.buguagaoshu.homework.evaluation.service.CourseTagService;
import com.buguagaoshu.homework.evaluation.vo.VerifyCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-08 18:55
 * 加载课程二级分类信息
 */
@RestController
@RequestMapping("/api")
public class CourseTagController {

    private final CourseTagCache courseTagCache;

    private final CourseTagService courseTagService;

    @Autowired
    public CourseTagController(CourseTagCache courseTagCache, CourseTagService courseTagService) {
        this.courseTagCache = courseTagCache;
        this.courseTagService = courseTagService;
    }

    @GetMapping("/course/tag/list")
    public ResponseDetails list() {
        return ResponseDetails.ok().put("data", courseTagCache.getCourseTagListTree());
    }


    @GetMapping("/course/tag/{id}")
    public ResponseDetails get(@PathVariable("id") Long id) {
        CourseTagEntity tagEntity = courseTagCache.getCourseTagEntityMap().get(id);
        if (tagEntity == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NOO_FOUND);
        }
        if (tagEntity.getCatelogId() != 0) {
            return ResponseDetails.ok().put("data", courseTagCache.getCourseTagMapHaveChildren().get(tagEntity.getCatelogId()));
        }
        return ResponseDetails.ok().put("data", courseTagCache.getCourseTagMapHaveChildren().get(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/course/tag/update")
    public ResponseDetails updateTag(@RequestBody CourseTagEntity courseTagEntity,
                                     HttpServletRequest request) {
        boolean result = courseTagService.updateTag(courseTagEntity, request);
        if (result) {
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(0, "出现异常，请稍后重试！");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/course/tag/save")
    public ResponseDetails save(@RequestBody CourseTagEntity courseTagEntity) {
        boolean result = courseTagService.saveTag(courseTagEntity);
        if (result) {
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(0, "夫分类不存在或提交的数据有误！");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/course/tag/delete/{id}")
    public ResponseDetails delete(@PathVariable("id") Long id,
                                  @RequestBody VerifyCodeVo verifyCodeVo,
                                  HttpServletRequest request) {
        if (courseTagService.deleteTag(id, verifyCodeVo.getVerifyCode(), request)) {
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(0, "夫分类不存在或提交的数据有误！");
    }
}
