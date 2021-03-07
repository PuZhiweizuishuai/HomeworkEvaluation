package com.buguagaoshu.homework.danmaku.repository;

import com.buguagaoshu.homework.danmaku.damain.DanmakuList;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-07 22:09
 */
public interface DanmakuListRepository extends MongoRepository<DanmakuList, Long> {
}
