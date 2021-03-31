package com.buguagaoshu.homework.search.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-25 17:37
 */
@Configuration
public class ElasticsearchConfig {

    private final ElasticsearchProperties elasticsearchProperties;

    @Autowired
    public ElasticsearchConfig(ElasticsearchProperties elasticsearchProperties) {
        this.elasticsearchProperties = elasticsearchProperties;
    }

    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestClientBuilder builder = null;
        if (elasticsearchProperties.getScheme() == null) {
            elasticsearchProperties.setScheme("http");
        }
        if (elasticsearchProperties.getUrl() != null) {
            if (elasticsearchProperties.getUrl().size() == 0) {
                builder = RestClient.builder(
                        new HttpHost("localhost", 9200, "http"));
            } else {
                List<HttpHost> httpHosts = new ArrayList<>();
                for (String host : elasticsearchProperties.getUrl()) {
                    HttpHost httpHost = new HttpHost(getHost(host), getPort(host), elasticsearchProperties.getScheme());
                    httpHosts.add(httpHost);
                }
                HttpHost[] array = httpHosts.toArray(new HttpHost[0]);
                builder = RestClient.builder(array);
            }
        } else {
            builder = RestClient.builder(
                    new HttpHost("localhost", 9200, "http"));
        }
        return new RestHighLevelClient(builder);
    }


    public int getPort(String url) {
        return Integer.parseInt(url.substring(url.lastIndexOf(":") + 1));
    }

    public String getHost(String url) {
        return url.substring(0, url.lastIndexOf(":"));
    }
}
