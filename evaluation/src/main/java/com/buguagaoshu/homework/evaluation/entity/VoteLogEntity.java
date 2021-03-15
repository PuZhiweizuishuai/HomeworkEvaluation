package com.buguagaoshu.homework.evaluation.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;
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
     * 投票的目标帖子ID
     * */
    @NotNull(message = "帖子ID不能为空！")
    private Long articleId;

    /**
     * 选择的项目
     * */
    @NotNull(message = "选项不能为空！")
    private List<UserVoteChoices> choices;


    private Long createTime;

    private String userId;
}
