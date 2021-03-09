package com.buguagaoshu.homework.danmaku.service.impl;

import com.buguagaoshu.homework.danmaku.damain.Danmaku;
import com.buguagaoshu.homework.danmaku.damain.DanmakuList;
import com.buguagaoshu.homework.danmaku.repository.DanmakuListRepository;
import com.buguagaoshu.homework.danmaku.repository.DanmakuRepository;
import com.buguagaoshu.homework.danmaku.service.DanmakuService;
import com.buguagaoshu.homework.danmaku.util.BuildBackInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-07 22:13
 */
@Service
@Slf4j
public class DanmakuServiceImpl implements DanmakuService {

    private final DanmakuRepository danmakuRepository;

    private final DanmakuListRepository danmakuListRepository;

    private final MongoTemplate mongoTemplate;

    private final BuildBackInfo buildBackInfo;

    public DanmakuServiceImpl(DanmakuRepository danmakuRepository, DanmakuListRepository danmakuListRepository, MongoTemplate mongoTemplate, BuildBackInfo buildBackInfo) {
        this.danmakuRepository = danmakuRepository;
        this.danmakuListRepository = danmakuListRepository;
        this.mongoTemplate = mongoTemplate;
        this.buildBackInfo = buildBackInfo;
    }

    @Override
    public boolean save(Danmaku danmaku) {
        // 保存详细弹幕数据，方便教师在后台对弹幕信息进行删除于查看
        danmaku.setCreateTime(System.currentTimeMillis());
        danmakuRepository.save(danmaku);
        // 保存前端需要展示的弹幕数据，提高性能
        Optional<DanmakuList> danmakuList = danmakuListRepository.findById(danmaku.getCoursewareId());
        if (danmakuList.isPresent()) {
            // 追加保存弹幕数据
            // TODO 限制保存数量
            Query query = new Query(Criteria.where("id").is(danmaku.getCoursewareId()));
            Update update = new Update().push("data", buildBackInfo.createDanmaku(danmaku));
            mongoTemplate.upsert(query, update, DanmakuList.class);
        } else {
            DanmakuList danmakus = new DanmakuList();
            danmakus.setId(danmaku.getCoursewareId());
            List<List<Object>> list = new LinkedList<>();
            list.add(buildBackInfo.createDanmaku(danmaku));
            danmakus.setData(list);
            danmakuListRepository.save(danmakus);
        }
        return true;
    }

    @Override
    public boolean delete(Danmaku danmaku) {
        try {
            if (danmaku == null) {
                return false;
            }
            danmakuRepository.delete(danmaku);
            // 清楚缓存列表数据
            Query query = new Query(Criteria.where("id").is(danmaku.getCoursewareId()));
            Update update = new Update().pull("data", buildBackInfo.createDanmaku(danmaku));
            mongoTemplate.upsert(query, update, DanmakuList.class);
            return true;
        } catch (Exception e) {
            log.error("弹幕数据不存在 {}, 发送错误： {}", danmaku.toString(), e.getMessage());
            return false;
        }
    }

    @Override
    public List<List<Object>> danmakuList(Long coursewareId) {
        Optional<DanmakuList> list = danmakuListRepository.findById(coursewareId);
        return list.map(DanmakuList::getData).orElse(new ArrayList<>());
    }
}
