package com.buguagaoshu.homework.oss;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OssApplicationTests {


    private String aes;

    @Value("${Aes.key}")
    public void setAes(String aes) {
        this.aes = aes;
    }

    @Test
    void contextLoads() {
        System.out.println(aes);
    }

}
