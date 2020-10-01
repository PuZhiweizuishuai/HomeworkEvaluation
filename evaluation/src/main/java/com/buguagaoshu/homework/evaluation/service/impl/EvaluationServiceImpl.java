package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.buguagaoshu.homework.common.enums.EvaluationType;
import com.buguagaoshu.homework.common.enums.HomeworkSubmitStatusEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitHomeworkStatusEntity;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-10-01 15:08
 */
@Service
@Slf4j
public class EvaluationServiceImpl implements EvaluationService {

    private final HomeworkService homeworkService;

    private final StudentsCurriculumService studentsCurriculumService;

    private final SubmitHomeworkStatusService submitHomeworkStatusService;


    private final UserService userService;

    @Autowired
    public EvaluationServiceImpl(HomeworkService homeworkService, StudentsCurriculumService studentsCurriculumService, SubmitHomeworkStatusService submitHomeworkStatusService, UserService userService) {
        this.homeworkService = homeworkService;
        this.studentsCurriculumService = studentsCurriculumService;
        this.submitHomeworkStatusService = submitHomeworkStatusService;
        this.userService = userService;
    }

    @Override
    public PageUtils list(Long courseId, Map<String, Object> params, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), courseId);
        // 检查权限
        if (studentsCurriculumEntity == null) {
            return null;
        }
        QueryWrapper<HomeworkEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("class_number", courseId);
        wrapper.eq("evaluation", EvaluationType.OPEN_EVALUATION.getCode());
        wrapper.le("close_time", System.currentTimeMillis());
        wrapper.orderByDesc("create_time");
        IPage<HomeworkEntity> page = homeworkService.page(
                new Query<HomeworkEntity>().getPage(params),
                wrapper
        );
        page.getRecords().forEach((p)->{
            p.setStatus(EvaluationType.EVALUATION_START.getCode());
        });
        return new PageUtils(page);
    }

    @Override
    public PageUtils userSubmitList(Long homeworkId, Map<String, Object> params, HttpServletRequest request) {
        long time = System.currentTimeMillis();
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        HomeworkEntity homeworkEntity = homeworkService.getById(homeworkId);
        if (homeworkEntity == null) {
            return null;
        }
        // 确保作业开启评价功能,以及作业已经结束
        if (homeworkEntity.getEvaluation().equals(EvaluationType.OPEN_EVALUATION.getCode()) && homeworkEntity.getCloseTime() <= time) {
            StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), homeworkEntity.getClassNumber());
            if (studentsCurriculumEntity == null) {
                return null;
            }
            QueryWrapper<SubmitHomeworkStatusEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("homework_id", homeworkId);
            wrapper.and(
                    status -> status.eq("status", HomeworkSubmitStatusEnum.SUBMIT.getCode())
                            .or()
                            .eq("status", HomeworkSubmitStatusEnum.COMPLETE.getCode())
            );
            wrapper.orderByDesc("submit_time");
            IPage<SubmitHomeworkStatusEntity> page = submitHomeworkStatusService.page(
                    new Query<SubmitHomeworkStatusEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }
        return null;

    }

    @Override
    public HomeworkModel userSubmitInfo(Long submitId, HttpServletRequest request) {
        long time = System.currentTimeMillis();
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        SubmitHomeworkStatusEntity userSubmit = submitHomeworkStatusService.getById(submitId);
        if (userSubmit == null) {
            return null;
        }
        HomeworkEntity homeworkEntity = homeworkService.getById(userSubmit.getHomeworkId());
        // 保证作业是开启评论的，并且作业已结束
        if (homeworkEntity.getEvaluation().equals(EvaluationType.OPEN_EVALUATION.getCode()) && homeworkEntity.getCloseTime() <= time) {
            // 学生在班级内
            StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), homeworkEntity.getClassNumber());
            if (studentsCurriculumEntity != null) {
                try {
                    HomeworkModel homeworkModel = homeworkService.homeworkQuestionList(homeworkEntity, userSubmit.getUserId(), true, userSubmit.getStudentName(), false);
                    UserEntity userEntity = userService.getById(userSubmit.getUserId());
                    homeworkModel.setUserAvatar(userEntity.getUserAvatarUrl());
                    return homeworkModel;
                } catch (Exception e) {
                    log.error("用户 {} 获取 {} 提交的作业详情时出现 {} 异常。", user.getId(), userSubmit.getUserId(), e.getMessage());
                    throw new UserDataFormatException("获取用户提交反序列化异常，请稍后重试！");
                }
            }
        }
        return null;
    }
}
