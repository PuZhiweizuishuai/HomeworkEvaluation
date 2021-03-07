package com.buguagaoshu.homework.danmaku;

import com.buguagaoshu.homework.danmaku.damain.Danmaku;
import com.buguagaoshu.homework.danmaku.repository.DanmakuRepository;
import com.buguagaoshu.homework.danmaku.service.DanmakuService;
import com.buguagaoshu.homework.danmaku.util.BuildBackInfo;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;


@SpringBootTest
class DanmakuApplicationTests {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    BuildBackInfo buildBackInfo;

    @Autowired
    DanmakuRepository danmakuRepository;

    @Autowired
    DanmakuService danmakuService;

    @Test
    void contextLoads() {
//        Danmaku danmaku = new Danmaku();
//        danmaku.setColor("ffffff");
//        danmaku.setAuthor("201741010102");
//        danmaku.setColorDec(16777215L);
//        danmaku.setCoursewareId(1L);
//        danmaku.setText("第二个弹幕测试");
//        danmaku.setTime(6.788627);
//        danmaku.setType(0);
        Optional<Danmaku> danmaku = danmakuRepository.findById(new ObjectId("6044e806a2e7fb3e8305d813"));
        danmakuService.delete(danmaku.get());

//        Query query = new Query(Criteria.where("coursewareId").is(2L));
//        System.out.println("result:" + mongoTemplate.find(query, Danmaku.class));
//        System.out.println("danmakuRepository:" + danmakuRepository.findByCoursewareId(2L));



//        danmaku.setBackInfo(buildBackInfo.createDanmaku(danmaku));
////        danmakuRepository.save(danmaku);
//        //System.out.println(danmakuRepository.findByCoursewareId(1L));
//        Query query = new Query(Criteria.where("coursewareId").is(1L));
//        Update update = new Update().push("backInfo", danmaku.getBackInfo());
//        mongoTemplate.upsert(query, update, Danmaku.class);
//       // query.fields().include("backInfo");
////        List<Danmaku> list = mongoTemplate.find(query, Danmaku.class);
//        //System.out.println(mongoTemplate.find(query, Danmaku.class));
//        DanmakuNew danmakuNew = new DanmakuNew();
//        danmakuNew.setCoursewareId(1L);
//        danmakuNew.setId(1L);
//        List<List<Object>> list = new LinkedList<>();
//        list.add(buildBackInfo.createDanmaku(danmaku));
//        danmakuNew.setData(list);
//        danmakuNewRepository.save(danmakuNew);
//        Query query = new Query(Criteria.where("coursewareId").is(1L));
//        Update update = new Update().push("data", danmaku.getBackInfo());
//        System.out.println(danmaku.getBackInfo());
//        mongoTemplate.upsert(query, update, DanmakuNew.class);


//        Query query = new Query(Criteria.where("coursewareId").is(1L));
//        Update update = new Update()
    }

}
