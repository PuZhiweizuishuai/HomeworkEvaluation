package com.buguagaoshu.homework.evaluation.config;

import com.buguagaoshu.homework.evaluation.cache.ArticleTagCache;
import com.buguagaoshu.homework.evaluation.cache.CourseTagCache;
import com.buguagaoshu.homework.evaluation.cache.WebsiteIndexMessageCache;
import com.buguagaoshu.homework.evaluation.entity.CourseTagEntity;
import com.buguagaoshu.homework.evaluation.service.AdvertisementService;
import com.buguagaoshu.homework.evaluation.service.ArticleService;
import com.buguagaoshu.homework.evaluation.service.ArticleTagService;
import com.buguagaoshu.homework.evaluation.service.CourseTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-04-08 18:30
 * 系统启动时初始化设置
 */
@Configuration
@Slf4j
public class WebInitializationConfig {


    @Bean
    public CommandLineRunner dataLoader(CourseTagCache courseTagCache,
                                        CourseTagService courseTagService,
                                        ArticleTagCache articleTagCache,
                                        ArticleTagService articleTagService,
                                        AdvertisementService advertisementService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                // 初始化课程分类树
                List<CourseTagEntity> list = courseTagService.listWithTree();
                courseTagCache.setCourseTagListTree(list);
                courseTagCache.setCourseTagEntityMap(courseTagService.CourseTagMap());
                courseTagCache.setCourseTagMapHaveChildren(list);
                log.info("初始化课程分类树");
                // 初始化帖子分类
                articleTagCache.setArticleTagCaches(articleTagService.listTree());
                articleTagCache.setIntegerArticleTagEntityMap(articleTagService.listToMap());
                articleTagCache.setFatherMap(articleTagCache.getArticleTagCaches());
                log.info("初始化话题分类！");
                // 初始化缓存
                advertisementService.addAdCache();
                log.info("初始化广告以及大图缓存");
            }
        };
    }
}
