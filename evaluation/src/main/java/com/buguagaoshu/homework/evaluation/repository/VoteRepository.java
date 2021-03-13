package com.buguagaoshu.homework.evaluation.repository;

import com.buguagaoshu.homework.evaluation.entity.VoteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-13 22:45
 */
public interface VoteRepository extends MongoRepository<VoteEntity, Long> {
}
