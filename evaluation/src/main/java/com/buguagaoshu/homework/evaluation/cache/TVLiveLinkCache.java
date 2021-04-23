package com.buguagaoshu.homework.evaluation.cache;

import com.buguagaoshu.homework.evaluation.entity.TVLiveLinkEntity;
import lombok.Data;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-23 21:55
 */
@Component
@Data
public class TVLiveLinkCache {
    private List<TVLiveLinkEntity> tvList;
}
