package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.buguagaoshu.homework.common.enums.*;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.CommentModel;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.EvaluationCommentVo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    private final NotificationService notificationService;


    private final CommentService commentService;

    private final VerifyCodeService verifyCodeService;


    private final UserService userService;

    @Autowired
    public EvaluationServiceImpl(HomeworkService homeworkService, StudentsCurriculumService studentsCurriculumService, SubmitHomeworkStatusService submitHomeworkStatusService, NotificationService notificationService, CommentService commentService, VerifyCodeService verifyCodeService, UserService userService) {
        this.homeworkService = homeworkService;
        this.studentsCurriculumService = studentsCurriculumService;
        this.submitHomeworkStatusService = submitHomeworkStatusService;
        this.notificationService = notificationService;
        this.commentService = commentService;
        this.verifyCodeService = verifyCodeService;
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
        if (!HomeworkSubmitStatusEnum.chekEvaluationPower(userSubmit.getStatus())) {
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

    @Override
    public PageUtils commentPage(Long submitId, Map<String, Object> params, HttpServletRequest request) {
        long time = System.currentTimeMillis();
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        SubmitHomeworkStatusEntity userSubmit = submitHomeworkStatusService.getById(submitId);
        if (userSubmit == null) {
            return null;
        }
        if (!HomeworkSubmitStatusEnum.chekEvaluationPower(userSubmit.getStatus())) {
            return null;
        }
        HomeworkEntity homeworkEntity = homeworkService.getById(userSubmit.getHomeworkId());
        // 保证作业是开启评论的，并且作业已结束
        if (homeworkEntity.getEvaluation().equals(EvaluationType.OPEN_EVALUATION.getCode()) && homeworkEntity.getCloseTime() <= time) {
            StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), homeworkEntity.getClassNumber());
            if (studentsCurriculumEntity == null) {
                return null;
            }
            return commentService.commentList(submitId, params, request, CommentTypeEnum.HOMEWORK_ONE_LEVEL_COMMENT, null);
        }
        return null;
    }

    @Override
    public PageUtils secondPage(Long commentId, Map<String, Object> params, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        // TODO 鉴权
        CommentEntity commentEntity = commentService.getById(commentId);
        if (commentEntity == null) {
            return null;
        }
        if (!commentEntity.getType().equals(CommentTypeEnum.HOMEWORK_ONE_LEVEL_COMMENT.getCode())) {
            return null;
        }
        SubmitHomeworkStatusEntity submit = submitHomeworkStatusService.getById(commentEntity.getArticleId());
        if (submit == null) {
            return null;
        }
        HomeworkEntity homeworkEntity = homeworkService.getById(submit.getHomeworkId());
        if (studentsCurriculumService.selectStudentByCurriculumId(user.getId(), homeworkEntity.getClassNumber()) == null) {
            return null;
        }
        return commentService.secondComment(commentId, CommentTypeEnum.HOMEWORK_SECOND_COMMENT ,params);
    }

    @Override
    public CommentModel saveComment(EvaluationCommentVo evaluationCommentVo, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String verifyCodeKey = (String) session.getAttribute(WebConstant.VERIFY_CODE_KEY);
        verifyCodeService.verify(verifyCodeKey, evaluationCommentVo.getVerifyCode());
        long time = System.currentTimeMillis();
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        SubmitHomeworkStatusEntity userSubmit = submitHomeworkStatusService.getById(evaluationCommentVo.getSubmitId());
        if (userSubmit == null) {
            return null;
        }
        if (!HomeworkSubmitStatusEnum.chekEvaluationPower(userSubmit.getStatus())) {
            return null;
        }
        HomeworkEntity homeworkEntity = homeworkService.getById(userSubmit.getHomeworkId());
        // 保证作业是开启评论的，并且作业已结束
        if (homeworkEntity.getEvaluation().equals(EvaluationType.OPEN_EVALUATION.getCode()) && homeworkEntity.getCloseTime() <= time) {
            StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), homeworkEntity.getClassNumber());
            if (studentsCurriculumEntity == null) {
                return null;
            }
            // 判断当前学生在这个作业中有没有提交，没有提交则没有评论的权力
            if (!userSubmit.getUserId().equals(user.getId())) {
                StudentsCurriculumEntity students = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), homeworkEntity.getClassNumber());
                if (!students.getRole().equals(RoleTypeEnum.TEACHER.getRole())) {
                    SubmitHomeworkStatusEntity nowUserSubmit = submitHomeworkStatusService.findUserSubmit(user.getId(), homeworkEntity.getId());
                    if (!HomeworkSubmitStatusEnum.chekEvaluationPower(nowUserSubmit.getStatus())) {
                        return null;
                    }
                }
            }
            return saveComment(evaluationCommentVo, user, request, userSubmit, homeworkEntity);
        }
        return null;
    }


    public CommentModel saveComment(EvaluationCommentVo evaluationCommentVo,
                             Claims user,
                             HttpServletRequest request,
                             SubmitHomeworkStatusEntity userSubmit,
                                    HomeworkEntity homeworkEntity) {

        CommentModel commentModel = new CommentModel();
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.initComment();
        CommentEntity father = null;
        // 一级评论
        if (evaluationCommentVo.getType().equals(CommentTypeEnum.HOMEWORK_ONE_LEVEL_COMMENT.getCode())) {
            BeanUtils.copyProperties(evaluationCommentVo, commentEntity);
            commentEntity.setArticleId(userSubmit.getId());
            commentEntity.setFatherId(evaluationCommentVo.getSubmitId());
            commentEntity.setType(CommentTypeEnum.HOMEWORK_ONE_LEVEL_COMMENT.getCode());
            commentEntity.setCommentId(null);
            // 二级评论
        } else {
            if (evaluationCommentVo.getCommentId() == null) {
                throw new UserDataFormatException("缺少评论对象，请查看提交数据或刷新后重试！");
            }
            father = commentService.getById(evaluationCommentVo.getCommentId());
            if (father != null && father.getStatus() == 0 && father.getArticleId().equals(evaluationCommentVo.getSubmitId())) {
                BeanUtils.copyProperties(evaluationCommentVo, commentEntity);
                commentEntity.setArticleId(evaluationCommentVo.getSubmitId());
            } else {
                throw new UserDataFormatException("评论的回复已被删除或没有权限");
            }
            // 判断父级ID
            // 如果评论的对象父级ID是文章ID，那么新评论的父级ID就应该是评论的评论ID
            if (father.getType().equals(CommentTypeEnum.HOMEWORK_ONE_LEVEL_COMMENT.getCode())) {
                commentEntity.setFatherId(father.getId());
            } else {
                commentEntity.setFatherId(father.getFatherId());
                commentService.countAdd("comment_count", father.getId(), 1);
            }
            commentService.countAdd("comment_count", commentEntity.getFatherId(), 1);
        }
        commentEntity.setAuthorId(user.getId());
        commentEntity.setIp(IpUtil.getIpAddr(request));
        commentEntity.setUa(request.getHeader("user-agent"));
        commentService.save(commentEntity);
        BeanUtils.copyProperties(commentEntity, commentModel);
        UserEntity userEntity = userService.getById(user.getId());
        commentModel.setAuthorId(userEntity.getUserId());
        commentModel.setUsername(userEntity.getUsername());
        commentModel.setAvatarUrl(userEntity.getUserAvatarUrl());
        // 评论数加1
        submitHomeworkStatusService.addCount("comment_count", evaluationCommentVo.getSubmitId(), 1);
        notificationService.sendComment(user.getId(), user.getSubject(), userSubmit.getUserId(), userSubmit,commentEntity, homeworkEntity);
        return commentModel;
    }
}
