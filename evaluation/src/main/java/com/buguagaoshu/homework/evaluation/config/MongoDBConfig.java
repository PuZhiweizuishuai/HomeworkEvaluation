package com.buguagaoshu.homework.evaluation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import javax.annotation.PostConstruct;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-14 22:07
 */
@Configuration
public class MongoDBConfig {
    private final MappingMongoConverter mappingMongoConverter;

    @Autowired
    public MongoDBConfig(MappingMongoConverter mappingMongoConverter) {
        this.mappingMongoConverter = mappingMongoConverter;
    }


    @PostConstruct
    public void setUpMap() {
        mappingMongoConverter.setMapKeyDotReplacement("-\\*dot\\*-");
    }
}
