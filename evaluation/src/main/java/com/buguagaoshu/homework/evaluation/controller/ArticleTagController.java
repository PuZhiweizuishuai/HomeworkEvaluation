package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.cache.ArticleTagCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/article/tags")
    public ResponseDetails list() {
        return ResponseDetails.ok().put("data", articleTagCache.getArticleTagCaches());
    }
}
