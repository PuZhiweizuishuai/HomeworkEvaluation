package com.buguagaoshu.homework.evaluation.schedule;

import com.buguagaoshu.homework.evaluation.service.ViewCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-12 17:30
 * 同步浏览量定时任务
 */
@Component
public class ViewCountSchedule {
    private final ViewCountService viewCountService;

    @Autowired
    public ViewCountSchedule(ViewCountService viewCountService) {
        this.viewCountService = viewCountService;
    }

    /**
     * 两小时同步一次
     * */
    @Scheduled(fixedRate = 7200000)
    public void viewCount() {
        viewCountService.synchronizedViewData();
    }
}
