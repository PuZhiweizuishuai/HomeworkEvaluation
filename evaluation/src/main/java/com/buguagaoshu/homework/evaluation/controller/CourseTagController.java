package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.cache.CourseTagCache;
import com.buguagaoshu.homework.evaluation.entity.CourseTagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-08 18:55
 * 加载课程二级分类信息
 */
@RestController
@RequestMapping("/api")
public class CourseTagController {

    private final CourseTagCache courseTagCache;

    @Autowired
    public CourseTagController(CourseTagCache courseTagCache) {
        this.courseTagCache = courseTagCache;
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
}
