package com.buguagaoshu.homework.evaluation.cache;

import com.buguagaoshu.homework.common.enums.Constant;
import com.buguagaoshu.homework.evaluation.entity.AdvertisementEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-10 16:19
 */
@Component
@Data
public class WebsiteIndexMessageCache {
    /**
     * 课程页大图及广告缓存
     * */
    Map<Long, AdvertisementEntity> curriculumAdList = new ConcurrentHashMap<>();


    /**
     * 主页大图及广告缓存
     * */
    Map<Long ,AdvertisementEntity> indexAdList = new ConcurrentHashMap<>();



    public void addCache(AdvertisementEntity advertisementEntity) {
        if (judgeRun(advertisementEntity)) {
            if (advertisementEntity.getType() == Constant.INDEX_AD
                    || advertisementEntity.getType() == Constant.INDEX_TOP_IMAGE) {
                indexAdList.put(advertisementEntity.getId(), advertisementEntity);
            } else {
                curriculumAdList.put(advertisementEntity.getId(), advertisementEntity);
            }
        }
    }

    public boolean judgeRun(AdvertisementEntity advertisementEntity) {
        long time = System.currentTimeMillis();
        return advertisementEntity.getStartTime() <= time && time < advertisementEntity.getEndTime();
    }

    public void remove(Long id) {
        indexAdList.remove(id);
        curriculumAdList.remove(id);
    }
}
