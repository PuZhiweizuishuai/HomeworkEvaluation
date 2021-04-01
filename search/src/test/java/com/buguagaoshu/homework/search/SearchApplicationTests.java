package com.buguagaoshu.homework.search;

import com.buguagaoshu.homework.search.config.ElasticsearchConfig;
import com.buguagaoshu.homework.search.serivce.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class SearchApplicationTests {

    @Autowired
    private SearchService searchService;

    @Test
    void searchTest() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("index", "article");
        map.put("key", "测试");
        System.out.println(searchService.notLoginSearchAll(map, null).getList());
    }


    @Test
    void contextLoads() throws IOException {
        IndexRequest indexRequest = new IndexRequest("test");
        indexRequest.id("10");
        User user = new User();
        user.setName("hahaha");
        user.setSex("男");
        user.setAge(20);
        ObjectMapper objectMapper = new ObjectMapper();
        indexRequest.source(objectMapper.writeValueAsString(user), XContentType.JSON);

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
