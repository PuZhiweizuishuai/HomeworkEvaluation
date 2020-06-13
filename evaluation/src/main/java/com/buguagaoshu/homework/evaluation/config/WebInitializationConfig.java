package com.buguagaoshu.homework.evaluation.config;

import com.buguagaoshu.homework.evaluation.cache.CourseTagCache;
import com.buguagaoshu.homework.evaluation.cache.WebsiteIndexMessageCache;
import com.buguagaoshu.homework.evaluation.service.AdvertisementService;
import com.buguagaoshu.homework.evaluation.service.CourseTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                                        AdvertisementService advertisementService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                // 初始化课程分类树
                courseTagCache.setCourseTagListTree(courseTagService.listWithTree());
                courseTagCache.setCourseTagEntityMap(courseTagService.CourseTagMap());
                log.info("初始化课程分类树");
                // 初始化缓存
                advertisementService.addAdCache();
                log.info("初始化广告以及大图缓存");
            }
        };
    }
}
