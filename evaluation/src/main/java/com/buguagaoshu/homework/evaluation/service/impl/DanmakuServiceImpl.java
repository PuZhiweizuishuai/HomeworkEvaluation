package com.buguagaoshu.homework.evaluation.service.impl;


import com.buguagaoshu.homework.common.domain.DanmakuDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.CoursewareEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.CoursewareService;
import com.buguagaoshu.homework.evaluation.service.DanmakuService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;



import javax.servlet.http.HttpServletRequest;

/**
 * @author Pu Zhiwei
 * */
@Service("danmakuService")
@Slf4j
public class DanmakuServiceImpl implements DanmakuService {

    private final CoursewareService coursewareService;

    private final StudentsCurriculumService studentsCurriculumService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public DanmakuServiceImpl(CoursewareService coursewareService, StudentsCurriculumService studentsCurriculumService, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.coursewareService = coursewareService;
        this.studentsCurriculumService = studentsCurriculumService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }




    @Override
    public List<Object> danmakuList(Long id, Integer max) {
        return null;
    }

    @Override
    public ReturnCodeEnum saveDanmaku(DanmakuDetails danmakuVo, HttpServletRequest request) {
        // System.out.println(danmakuVo.getToken());
        Claims claims = JwtUtil.parseJWT(TokenAuthenticationHelper.SECRET_KEY, danmakuVo.getToken());
        if (claims == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }

        CoursewareEntity byId = coursewareService.getById(danmakuVo.getId());
        if (byId == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(claims.getId(), byId.getCourseId());
        if (studentsCurriculumEntity == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        danmakuVo.setAuthor(claims.getId());
        try {
            kafkaTemplate.send("danmaku", objectMapper.writeValueAsString(danmakuVo));
            return ReturnCodeEnum.SUCCESS;
        } catch (Exception e) {
            log.error("弹幕数据序列化失败：{}", e.getMessage());
            return ReturnCodeEnum.SYSTEM_ERROR;
        }
    }

}