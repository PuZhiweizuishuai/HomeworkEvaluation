package com.buguagaoshu.homework.evaluation;


import com.buguagaoshu.homework.evaluation.service.ViewCountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-12 16:32
 */
@SpringBootTest
public class RedisTests {
    @Autowired
    ViewCountService viewCountService;

    @Test
    void contextLoads() {
        //viewCountService.synchronizedViewData();
    }
}
