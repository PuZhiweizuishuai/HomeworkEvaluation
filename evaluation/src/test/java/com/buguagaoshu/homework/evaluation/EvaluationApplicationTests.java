package com.buguagaoshu.homework.evaluation;

import com.buguagaoshu.homework.common.domain.ConvertOfficeInfo;
import com.buguagaoshu.homework.evaluation.entity.VoteEntity;
import com.buguagaoshu.homework.evaluation.entity.VoteLogEntity;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.service.VoteService;
import com.buguagaoshu.homework.evaluation.vo.VoteVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class EvaluationApplicationTests {
    @Autowired
    VoteService voteService;

    @Autowired
    MongoTemplate mongoTemplate;


    @Test
    void contextLoads() throws JsonProcessingException {
//        VoteVo vote = new VoteVo();
//        vote.setCreateTime(System.currentTimeMillis());
//        vote.setId(1L);
//        vote.setEndTime("2021-03-31 00:00:00");
//        vote.setTitle("test 投票");
//        List<String> list = new ArrayList<>();
//        list.add("a");
//        list.add("b");
//        list.add("c");
//        list.add("d");
//        vote.setChoices(list);
//        vote.setType(0);
//        vote.setVoteCount(0L);
//        voteService.save(vote);
//        VoteLogEntity voteLogEntity = new VoteLogEntity();
//        voteLogEntity.setVoteId(1L);
//        List<String> list = new ArrayList<>();
//        list.add("a");
//        voteLogEntity.setChoice(list);
//        voteService.vote(voteLogEntity, null);
//        Query query = new Query(Criteria.where("id").is(1L));
//        Update update = new Update().inc("choices.a", 1);
//        mongoTemplate.upsert(query, update, VoteEntity.class);

        Query query = new Query(Criteria.where("id").is(new ObjectId("604f37942642526c8efbf14f")));
        Update update = new Update();
        update.inc("choices." + "b", 1);
        update.inc("voteCount", 1);
        mongoTemplate.upsert(query, update, VoteEntity.class);
    }

}
