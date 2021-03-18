package com.buguagaoshu.homework.click;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClickApplicationTests {

    @Test
    void contextLoads() {
        // 1372216847549612033
        System.out.println(IdWorker.getId());
        System.out.println(IdWorker.getId());
    }

}
