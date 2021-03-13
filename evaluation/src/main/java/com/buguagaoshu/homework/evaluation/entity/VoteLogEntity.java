package com.buguagaoshu.homework.evaluation.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-13 23:15
 */
@Data
@Document("VoteLog")
public class VoteLogEntity {
    @MongoId
    private ObjectId id;

    /**
     * 投票的目标ID
     * */
    private Long voteId;

    /**
     * 选择的项目
     * */
    private List<String> choice;


    private Long createTime;

    private String userId;
}
