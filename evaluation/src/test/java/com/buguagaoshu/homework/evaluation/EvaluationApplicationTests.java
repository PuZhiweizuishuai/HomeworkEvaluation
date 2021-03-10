package com.buguagaoshu.homework.evaluation;

import com.buguagaoshu.homework.common.domain.ConvertOfficeInfo;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class EvaluationApplicationTests {
    @Autowired
    UserService userService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    StudentsCurriculumService studentsCurriculumService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;


    @Test
    void contextLoads() throws JsonProcessingException {
        ConvertOfficeInfo officeInfo = new ConvertOfficeInfo();
        officeInfo.setFilename("实现一个在线课程学习平台v2.docx");
        officeInfo.setFilePath("/home/puzhiwei/homework/");
        officeInfo.setTargetFilePath("/home/puzhiwei/homework/实现一个在线课程学习平台v2.pdf");
        kafkaTemplate.send("ConvertOffice", new ObjectMapper().writeValueAsString(officeInfo));
    }

}
