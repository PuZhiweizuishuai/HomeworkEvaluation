package com.buguagaoshu.homework.evaluation.entity;

import com.buguagaoshu.homework.common.valid.ListValue;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-13 22:40
 * 投票模块
 */
@Data
@Document("Votes")
public class VoteEntity {
    @MongoId
    private ObjectId id;

    private Long articleId;

    @Max(value = 50, message = "标题过长，最大不超过50个字！")
    private String title;

    @NotNull(message = "选项不能为空！")
    private Map<String, Long> choices;

    private Integer maxChoice;

    private Long endTime;

    @ListValue(value = {0, 1}, message = "类型取值错误")
    private Integer type;

    private Long createTime;

    private Long voteCount = 0L;
}
