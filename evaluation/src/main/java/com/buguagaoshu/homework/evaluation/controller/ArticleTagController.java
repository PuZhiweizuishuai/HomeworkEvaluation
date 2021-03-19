package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.cache.ArticleTagCache;
import com.buguagaoshu.homework.evaluation.entity.ArticleTagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-12 21:20
 */
@RestController
public class ArticleTagController {
    private final ArticleTagCache articleTagCache;

    @Autowired
    public ArticleTagController(ArticleTagCache articleTagCache) {
        this.articleTagCache = articleTagCache;
    }

    @GetMapping("/article/tags/tree")
    public ResponseDetails tree() {
        return ResponseDetails.ok().put("data", articleTagCache.getArticleTagCaches());
    }

    @GetMapping("/article/tags/list")
    public ResponseDetails list() {
        return ResponseDetails.ok().put("data", articleTagCache.getIntegerArticleTagEntityMap().values());
    }

    @GetMapping("/article/tags/info/{id}")
    public ResponseDetails getInfo(@PathVariable("id") Long id) {
        ArticleTagEntity articleTagEntity = articleTagCache.getIntegerArticleTagEntityMap().get(id);
        if (articleTagEntity == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NOO_FOUND);
        }
        Map<String, ArticleTagEntity> map = new HashMap<>(2);
        map.put("now", articleTagEntity);
        if (articleTagEntity.getCatelogId() != 0) {
            ArticleTagEntity father = articleTagCache.getIntegerArticleTagEntityMap().get(articleTagEntity.getCatelogId());
            map.put("father", father);
        } else {
            map.put("now", articleTagCache.getFatherMap().get(id));
        }
        return ResponseDetails.ok().put("data", map);
    }
}
