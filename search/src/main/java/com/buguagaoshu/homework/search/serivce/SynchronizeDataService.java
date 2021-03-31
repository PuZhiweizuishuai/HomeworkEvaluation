package com.buguagaoshu.homework.search.serivce;

import com.buguagaoshu.homework.common.enums.TableEventType;
import com.buguagaoshu.homework.common.search.ArticleSearchMapper;
import com.buguagaoshu.homework.common.search.CurriculumSearchMapper;
import com.buguagaoshu.homework.common.search.QuestionsSearchMapper;
import com.buguagaoshu.homework.common.search.SearchTableNameConstant;
import com.buguagaoshu.homework.common.utils.DoubleDeserializer;
import com.buguagaoshu.homework.common.utils.IntJsonDeserializer;
import com.buguagaoshu.homework.common.utils.LongJsonDeserializer;
import com.buguagaoshu.homework.search.config.ElasticsearchConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 15:02
 */
@Service
@Slf4j
public class SynchronizeDataService {
    private final ObjectMapper objectMapper;

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public SynchronizeDataService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Long.class, new LongJsonDeserializer());
        simpleModule.addDeserializer(Integer.class, new IntJsonDeserializer());
        simpleModule.addDeserializer(Double.class, new DoubleDeserializer());
        objectMapper.registerModule(simpleModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper = objectMapper;
    }

    public void save(String message) {
        HashMap<String, String> map = jsonToMap(message);
        if (map != null) {
            // 获取操作类型
            int eventType = Integer.parseInt(map.get(SearchTableNameConstant.EVENT_TYPE_KEY));
            // 获取表名
            String tableName = map.get(SearchTableNameConstant.TABLE_NAME_KEY);
            // 处理不同操作

            if (eventType == TableEventType.INSERT.getCode() || eventType == TableEventType.UPDATE.getCode()) {
                insert(map,message, tableName);
            } else if (eventType == TableEventType.DELETE.getCode()) {
                delete(map, tableName);
            }
        }
    }

    public String getSearchMessage(Map<String, String> map, String json, String tableName) {
        String message = "";
        switch (tableName) {
            case SearchTableNameConstant.ARTICLE:
                ArticleSearchMapper article = jsonToObjectEntity(json, ArticleSearchMapper.class);
                if (article == null) {
                    log.error("位于SynchronizeDataService类下77行的数据序列化失败！");
                    return "";
                }
                message = objToJson(article);
                break;
            case SearchTableNameConstant.QUESTIONS:
                QuestionsSearchMapper question = jsonToObjectEntity(json, QuestionsSearchMapper.class);
                if (question == null) {
                    log.error("位于SynchronizeDataService类下86行的数据序列化失败！");
                    return "";
                }
                message = objToJson(question);
                break;
            case SearchTableNameConstant.CURRICULUM:
                CurriculumSearchMapper curr = jsonToObjectEntity(json, CurriculumSearchMapper.class);
                if (curr == null) {
                    log.error("位于SynchronizeDataService类下94行的数据序列化失败！");
                    return "";
                }
                message = objToJson(curr);
                break;
            default:
                break;
        }
        return message;
    }

    public void insert(Map<String, String> map, String json, String tableName) {
        String message = getSearchMessage(map, json, tableName);
        if (message.equals("")){
            log.error("位于SynchronizeDataService类下108行的数据序列化失败！");
            return;
        }
        IndexRequest indexRequest = new IndexRequest(tableName);
        indexRequest.id(map.get("id"));
        indexRequest.source(message, XContentType.JSON);
        try {
            IndexResponse index = restHighLevelClient.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS);
            log.info(index.toString());
        } catch (IOException e) {
            log.error("发送保存请求失败，保存失败 ： {} 失败原因：{}", json, e.getMessage());
        }
    }

    public void delete(Map<String, String> map, String tableName) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(tableName, map.get("id"));
            DeleteResponse deleteResponse = restHighLevelClient.delete(
                    deleteRequest, ElasticsearchConfig.COMMON_OPTIONS);
            log.info(deleteResponse.toString());
        } catch (Exception e) {
            log.error("发送删除请求失败：失败表：{}, 失败ID： {} 失败原因：{}", tableName,map.get("id"), e.getMessage());
        }
    }



    private HashMap<String, String> jsonToMap(String json) {

        try {
            return objectMapper.readValue(json, HashMap.class);
        } catch (JsonProcessingException e) {
            log.error("json数据 {} 转换失败！,失败原因：{}", json, e.getMessage());
            return null;
        }
    }

    private <T> T jsonToObjectEntity(String json, Class<T> classType) {
        try {
            return objectMapper.readValue(json, classType);
        } catch (JsonProcessingException e) {
            log.error("json数据 {} 转换失败！,失败原因：{}", json, e.getMessage());
            return null;
        }
    }


    private String objToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("对象 {} 序列化失败，失败原因：{}", obj.getClass().getName(), e.getMessage());
            return "";
        }
    }
}
