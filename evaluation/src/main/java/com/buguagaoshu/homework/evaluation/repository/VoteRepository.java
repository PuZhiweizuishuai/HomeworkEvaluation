package com.buguagaoshu.homework.evaluation.repository;

import com.buguagaoshu.homework.evaluation.entity.VoteEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-13 22:45
 */
public interface VoteRepository extends MongoRepository<VoteEntity, ObjectId> {
    /**
     * 通过帖子ID查找投票
     * @param articleId 帖子ID
     * @return 投票列表
     * */
    List<VoteEntity> findVoteEntityByArticleId(Long articleId);
}
