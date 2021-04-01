package com.buguagaoshu.homework.search.serivce;

import com.buguagaoshu.homework.common.enums.ArticleTypeEnum;
import com.buguagaoshu.homework.common.search.SearchTableNameConstant;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.search.config.ElasticsearchConfig;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-01 12:40
 */
@Service
@Slf4j
public class SearchService {
    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public SearchService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }


    /**
     * 不登录可以搜索到的数据
     */
    public PageUtils notLoginSearchAll(Map<String, Object> params,
                                       HttpServletRequest request) {
        String index = (String) params.get("index");
        if (SearchTableNameConstant.notLoginSearch(index)) {
            SearchRequest searchRequest = createSearchRequest(params, index, false);
            SearchResponse searchResponse = sendSearch(searchRequest);
            return createPageData(searchResponse, params, index);
        }
        return null;
    }

    public PageUtils createPageData(SearchResponse searchResponse, Map<String, Object> params, String index) {
        // 查询时间
        long took = searchResponse.getTook().getMillis();

        // 总数
        long totalCount = searchResponse.getHits().getTotalHits().value;
        // 当前页
        int page = getInt((String) params.get("page")) + 1;
        // 大小

        int size = getInt((String) params.get("size"));

        if (size >= 50) {
            size = 50;
        } else if (size <= 0) {
            size = 20;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        // 检查是否需要删除正文，降低网络压力
        // 减小判断次数
        if (index.equals(SearchTableNameConstant.ARTICLE) && totalCount != 0) {
            // 检查类型
            if (!searchResponse.getHits().getHits()[0].getSourceAsMap().get("type").equals(ArticleTypeEnum.THINK.getCode())) {
                for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                    Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                    sourceAsMap.remove("content");
                    list.add(sourceAsMap);
                }
            } else {
                for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                    list.add(searchHit.getSourceAsMap());
                }
            }
        } else if (index.equals(SearchTableNameConstant.CURRICULUM) && totalCount != 0) {
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                sourceAsMap.remove("curriculumInfo");
                list.add(sourceAsMap);
            }
        } else {
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                list.add(searchHit.getSourceAsMap());
            }
        }

        PageUtils pageUtils = new PageUtils(list, totalCount, size, page);
        pageUtils.setTook(took);
        return pageUtils;
    }

    public SearchResponse sendSearch(SearchRequest searchRequest) {
        try {
            return restHighLevelClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("发送查询请求异常！请求数据{}， \n异常原因：{}", searchRequest.toString(), e.getMessage());
            return null;
        }

    }


    /**
     * 构造检索请求
     *
     * @param params          查询参数
     *                        key： 关键词，检索标题正文以及话题发帖人等信息
     *                        sort： 排序
     *                        0   默认相关度
     *                        1   时间倒序
     *                        page： 页码
     *                        size： 大小
     *                        schema: 模式 类型名
     *                        sex： 性别
     *                        index： 索引
     *                        搜索问题搜索时是问题类型
     *                        帖子搜索时是帖子类型
     * @param index           索引
     * @param hasCourseSearch 是否是课程内索引
     * @return 构造完成的检索请求
     */
    public SearchRequest createSearchRequest(Map<String, Object> params, String index, boolean hasCourseSearch) {
        int page = getInt((String) params.get("page"));
        if (page != 0) {
            page = page - 1;
        }
        int size = getInt((String) params.get("size"));
        int sort = getInt((String) params.get("sort"));
        if (size >= 50) {
            size = 50;
        } else if (size <= 0) {
            size = 20;
        }
        if (page >= 1) {
            page = page * size;
        }
        String schema = (String) params.get("schema");
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分页
        sourceBuilder.from(page);
        sourceBuilder.size(size);

        // 查询条件
        String key = (String) params.get("key");
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        // 构造不同查询条件
        switch (index) {
            case SearchTableNameConstant.ARTICLE:
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                // 构造复合查询
                MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(key, "title", "content", "tag", "authorId", "authorName");
                if (StringUtils.isEmpty(schema)) {
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders
                            .rangeQuery("type")
                            .gte(ArticleTypeEnum.ORDINARY.getCode())
                            .lt(ArticleTypeEnum.ORDINARY_END.getCode());
                    boolQueryBuilder.must().add(rangeQueryBuilder);
                } else {
                    if (String.valueOf(ArticleTypeEnum.THINK.getCode()).equals(schema)) {
                        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("type", ArticleTypeEnum.THINK.getCode());
                        boolQueryBuilder.must().add(termQueryBuilder);
                    }
                }
                boolQueryBuilder.must().add(queryBuilder);
                sourceBuilder.query(boolQueryBuilder);
                break;
            case SearchTableNameConstant.CURRICULUM:
                sourceBuilder.query(QueryBuilders.multiMatchQuery(key,
                        "createTeacher",
                        "teacherName",
                        "curriculumName",
                        "simpleInfo",
                        "curriculumInfo"));
                break;
            case SearchTableNameConstant.USER:
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(key, "userId", "username");
                String sex = (String) params.get("sex");
                if (!StringUtils.isEmpty(sex)) {
                    // 全匹配查询
                    boolQuery.must().add(QueryBuilders.termQuery("sex", sex));
                }
                boolQuery.must().add(multiMatchQueryBuilder);
                sourceBuilder.query(multiMatchQueryBuilder);
            default:
                break;
        }


        // 设置排序方法

        if (sort == 1) {
            String field = "updateTime";
            if (index.equals(SearchTableNameConstant.QUESTIONS) || index.equals(SearchTableNameConstant.USER)) {
                field = "createTime";
            }
            sourceBuilder.sort(new FieldSortBuilder(field).order(SortOrder.DESC));
        }

        searchRequest.source(sourceBuilder);
        return searchRequest;
    }


    public int getInt(String code) {
        int page = 0;
        if (!StringUtils.isEmpty(code)) {
            try {
                page = Integer.parseInt(code);
            } catch (Exception ignored) {
            }
            if (page <= 0) {
                page = 0;
            }
        }
        return page;
    }
}
