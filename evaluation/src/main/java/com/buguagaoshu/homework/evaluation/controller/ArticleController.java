package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.model.ArticleModel;
import com.buguagaoshu.homework.evaluation.service.ArticleService;
import com.buguagaoshu.homework.evaluation.vo.ArticleVo;
import com.buguagaoshu.homework.evaluation.vo.DeleteVo;
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


    @PostMapping("/article/delete")
    public ResponseDetails delete(@RequestBody DeleteVo deleteVo,
                                  HttpServletRequest request) {
        if (articleService.deleteArticle(deleteVo, request)) {
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
    }


    @PostMapping("/article/perfect")
    public ResponseDetails perfect(@RequestBody DeleteVo deleteVo,
                                   HttpServletRequest request) {
        if (articleService.perfect(deleteVo, request)) {
            return ResponseDetails.ok();
        }
        return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
    }

    @GetMapping("/article/list/course/{courseId}")
    public ResponseDetails courseList(@PathVariable("courseId") Long courseId,
                                @RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        PageUtils courseList = articleService.getCourseList(courseId, params, request);
        if (courseList == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
        }
        return ResponseDetails.ok().put("data", courseList);
    }


    /**
     * 获取课程内帖子的详细内容
     * */
    @GetMapping("/article/info/course/{id}")
    public ResponseDetails info(@PathVariable("id") Long id,
                                HttpServletRequest request) {
        ArticleModel articleModel = articleService.courseArticleInfo(id, request);
        if (articleModel != null) {
            return ResponseDetails.ok().put("data", articleModel);
        }
        return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
    }
}
