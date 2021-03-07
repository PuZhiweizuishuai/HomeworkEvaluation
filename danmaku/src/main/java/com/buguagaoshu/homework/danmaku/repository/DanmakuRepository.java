package com.buguagaoshu.homework.danmaku.repository;

import com.buguagaoshu.homework.danmaku.damain.Danmaku;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-06 22:41
 * 弹幕存储接口
 */
public interface DanmakuRepository extends MongoRepository<Danmaku, ObjectId> {
    List<Object> findByCoursewareId(Long id);
}
