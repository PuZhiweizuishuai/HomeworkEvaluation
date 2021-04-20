package com.buguagaoshu.homework.search;


import com.buguagaoshu.homework.search.serivce.SearchService;
import lombok.Data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


@SpringBootTest
class SearchApplicationTests {

    @Autowired
    private SearchService searchService;



    @Test
    void contextLoads() throws IOException {


        // 执行保存
        //IndexResponse index = restHighLevelClient.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS);
        //System.out.println(index.toString());
    }

    @Data
    class User {
        private String name;

        private String sex;

        private Integer age;
    }

}
