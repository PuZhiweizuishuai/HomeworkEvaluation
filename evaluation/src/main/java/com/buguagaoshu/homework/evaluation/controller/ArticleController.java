package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.service.ArticleService;
import com.buguagaoshu.homework.evaluation.vo.ArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-28 20:18
 * 发帖控制
 */
@RestController
public class ArticleController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/article/save")
    public ResponseDetails save(@Valid @RequestBody ArticleVo articleVo,
                                HttpServletRequest request) {

        ArticleEntity entity = articleService.saveArticle(articleVo, request);
        if (entity == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", entity);
    }

    @GetMapping("/article/list/{courseId}")
    public ResponseDetails list(@PathVariable("courseId") Long courseId,
                                @RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        PageUtils courseList = articleService.getCourseList(courseId, params, request);
        if (courseList == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", courseList);
    }
}
