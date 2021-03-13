package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.cache.WebsiteIndexMessageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-10 16:13
 * 加载主页，课程页等需要的各种信息
 */
@RestController
@RequestMapping("/index")
public class IndexController {
    private final WebsiteIndexMessageCache indexMessageCache;

    @Autowired
    public IndexController(WebsiteIndexMessageCache indexMessageCache) {
        this.indexMessageCache = indexMessageCache;
    }

    /**
     * 课程页所需要的所有数据
     * 广告
     * 课程页主页的大图
     * 课程分类
     *
     * */
    @GetMapping("/curriculum/ad")
    public ResponseDetails curriculumPageCarousel() {
        return ResponseDetails.ok().put("data", indexMessageCache.getCurriculumAdList().values());
    }

    @GetMapping("/bbs/ad")
    public ResponseDetails bbsPageCarousel() {
        return ResponseDetails.ok().put("data", indexMessageCache.getIndexAdList().values());
    }
}
