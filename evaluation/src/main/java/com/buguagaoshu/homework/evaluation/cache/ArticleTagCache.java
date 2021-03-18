package com.buguagaoshu.homework.evaluation.cache;

import com.buguagaoshu.homework.evaluation.entity.ArticleTagEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-28 20:56
 */
@Data
@Component
public class ArticleTagCache {
    private List<ArticleTagEntity> articleTagCaches;


    private Map<Long, ArticleTagEntity> integerArticleTagEntityMap;
}
