package com.buguagaoshu.homework.evaluation.repository;

import com.buguagaoshu.homework.evaluation.entity.VoteLogEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-13 23:17
 */
public interface VoteLogRepository extends MongoRepository<VoteLogEntity, ObjectId> {
    VoteLogEntity findVoteLogEntityByArticleIdAndUserId(Long articleId, String userId);
}
