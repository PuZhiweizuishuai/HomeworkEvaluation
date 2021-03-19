package com.buguagaoshu.homework.evaluation.cache;

import com.buguagaoshu.homework.evaluation.entity.ArticleTagEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-28 20:56
 */
@Data
@Component
public class ArticleTagCache {
    private List<ArticleTagEntity> articleTagCaches;


    private Map<Long, ArticleTagEntity> integerArticleTagEntityMap;


    private Map<Long, ArticleTagEntity> fatherMap;

    public void setFatherMap(List<ArticleTagEntity> articleTagCaches) {
        this.fatherMap = new HashMap<>(articleTagCaches.size());
        for (ArticleTagEntity a : articleTagCaches) {
            this.fatherMap.put(a.getId(), a);
        }
    }

    public List<Long> getTagId(Long id) {
        ArticleTagEntity articleTagEntity = this.integerArticleTagEntityMap.get(id);
        List<Long> ids = new ArrayList<>();
        if (articleTagEntity.getCatelogId() == 0) {
            ArticleTagEntity father = this.fatherMap.get(id);
            father.getChildren().forEach((f) -> {
                ids.add(f.getId());
            });
        } else {
            ids.add(articleTagEntity.getId());
        }
        return ids;
    }
}
