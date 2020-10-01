package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.MultipleReturnValues;
import com.buguagaoshu.homework.common.enums.*;
import com.buguagaoshu.homework.common.valid.OnlyNumber;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.HomeworkAnswer;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import com.buguagaoshu.homework.evaluation.vo.KeeperDashboardViewVo;
import com.buguagaoshu.homework.evaluation.vo.QuestionComment;
import com.buguagaoshu.homework.evaluation.vo.TeacherCommentHomeworkData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.HomeworkDao;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * @author puzhiwei
 */
@Slf4j
@Service("homeworkService")
public class HomeworkServiceImpl extends ServiceImpl<HomeworkDao, HomeworkEntity> implements HomeworkService {

    private final static String INTO_HOMEWORK_TIME_NAME = "intoTime";

    private CurriculumService curriculumService;

    private StudentsCurriculumService studentsCurriculumService;

    private QuestionsService questionsService;

    private HomeworkWithQuestionsService homeworkWithQuestionsService;

    private SubmitHomeworkStatusService submitHomeworkStatusService;

    private SubmitQuestionsService submitQuestionsService;

    private NotificationService notificationService;

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setQuestionsService(SubmitQuestionsService submitQuestionsService) {
        this.submitQuestionsService = submitQuestionsService;
    }

    @Autowired
    public void setSubmitHomeworkStatusService(SubmitHomeworkStatusService submitHomeworkStatusService) {
        this.submitHomeworkStatusService = submitHomeworkStatusService;
    }

    @Autowired
    public void setHomeworkWithQuestionsService(HomeworkWithQuestionsService homeworkWithQuestionsService) {
        this.homeworkWithQuestionsService = homeworkWithQuestionsService;
    }

    @Autowired
    public void setQuestionsService(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @Autowired
    public void setCurriculumService(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @Autowired
    public void setStudentsCurriculumService(StudentsCurriculumService studentsCurriculumService) {
        this.studentsCurriculumService = studentsCurriculumService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HomeworkEntity> page = this.page(
                new Query<HomeworkEntity>().getPage(params),
                new QueryWrapper<HomeworkEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    @Transactional(rollbackFor = UserDataFormatException.class)
    public HomeworkModel add(HomeworkModel homeworkModel, Claims nowLoginUser) {
        // 检查有无权限进行创建作业
        CurriculumEntity curriculumEntity = curriculumService.getById(homeworkModel.getClassNumber());
        if (!nowLoginUser.getId().equals(curriculumEntity.getCreateTeacher())) {
            // 查询是否拥有班级教师权限
            if (!studentsCurriculumService.checkThisCurriculumHaveTeacher(curriculumEntity.getId(), nowLoginUser.getId())) {
                return null;
            }
        }
        // 拥有创建作业的权限，开始创建作业
        // 数据校验
        HomeworkEntity homeworkEntity = homeworkHandleValid(homeworkModel);
        // 需要写入创建时间，分值，教师，状态
        homeworkEntity.setCreateTime(System.currentTimeMillis());
        homeworkEntity.setCreateTeacher(nowLoginUser.getId());
        homeworkEntity.setStatus(calculationStatus(homeworkEntity));
        int score = 0;
        // 存储作业获得主键 ID
        this.save(homeworkEntity);
        // 校验问题
        // 新建的问题列表
        List<QuestionsEntity> questionsEntityList = new ArrayList<>();
        // 导入的问题列表
        List<HomeworkWithQuestionsEntity> homeworkWithQuestionsEntities = new ArrayList<>();
        if (homeworkModel.getQuestionsModels() != null && homeworkModel.getQuestionsModels().size() != 0) {
            for (QuestionsModel questionsModel : homeworkModel.getQuestionsModels()) {
                score += questionsModel.getScore();
                // 导入已有问题
                if (questionsModel.getId() != null) {
                    if (questionsService.checkUseQuestionPower(questionsModel.getId(), nowLoginUser.getId())) {
                        // 将数据加入题目列表
                        homeworkWithQuestionsEntities.add(getHomeworkWithQuestionsEntity(homeworkEntity.getId(),
                                questionsModel.getId(),
                                nowLoginUser.getId(),
                                questionsModel.getScore()));
                    } else {
                        throw new UserDataFormatException("没有导入这道题目的权限， 题目内容： " + questionsModel.getQuestion());
                    }
                    // 新建的问题
                } else {
                    // 校验当前问题
                    questionsService.questionHandleValid(questionsModel);
                    if (questionsModel.getScore() == null || questionsModel.getScore() <= 0) {
                        throw new UserDataFormatException("题目分数必须大于 0");
                    }
                    try {
                        QuestionsEntity questionsEntity = questionsService.copyQuestionModeToEntity(questionsModel, nowLoginUser.getId());
                        questionsEntity.setScore(questionsModel.getScore());
                        questionsEntityList.add(questionsEntity);
                        // 保存要添加的问题
                    } catch (JsonProcessingException e) {
                        throw new UserDataFormatException("像数据库存入题目时序列化异常，请稍后重试！");
                    }
                }
            }

        } else {
            throw new UserDataFormatException("没有提交题目数据！");
        }
        // 写入成绩
        homeworkEntity.setScore(score);
        // 更新作业数据
        this.updateById(homeworkEntity);
        // 保存新建的题目
        if (questionsEntityList.size() != 0) {
            questionsService.saveBatch(questionsEntityList);
            questionsEntityList.forEach((q) -> {
                homeworkWithQuestionsEntities.add(getHomeworkWithQuestionsEntity(homeworkEntity.getId(),
                        q.getId(),
                        nowLoginUser.getId(),
                        q.getScore()));
            });
        }

        // 保存作业与题目的关联
        homeworkWithQuestionsService.saveBatch(homeworkWithQuestionsEntities);
        // 通知班级成员
        List<StudentsCurriculumEntity> userListInCurriculum = studentsCurriculumService.findUserListInCurriculum(homeworkEntity.getClassNumber());
        notificationService.sendNewExam(userListInCurriculum, nowLoginUser.getId(), nowLoginUser.getSubject(), curriculumEntity, homeworkEntity);
        homeworkModel.setId(homeworkEntity.getId());
        return homeworkModel;
    }

    @Override
    public PageUtils courseHomeworkList(Long courseId, Map<String, Object> params, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        if (!judgeUserIsInCourse(courseId, user.getId())) {
            return null;
        }
        QueryWrapper<HomeworkEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("class_number", courseId);
        wrapper.orderByDesc("create_time");
        IPage<HomeworkEntity> page = this.page(
                new Query<HomeworkEntity>().getPage(params),
                wrapper
        );
        if (page.getTotal() == 0) {
            return new PageUtils(page);
        }

        page.getRecords().forEach((l) -> {
            l.setStatus(calculationStatus(l));
        });
        return new PageUtils(page);
    }

    /**
     * 学生是否有获取题目的权限
     *
     * @param homeworkId 作业ID
     */
    public HomeworkEntity haveGetQuestionListPower(Long homeworkId) {
        // 判断作业是否存在
        HomeworkEntity homeworkEntity = this.getById(homeworkId);
        if (homeworkEntity == null) {
            return null;
        }
        long time = System.currentTimeMillis();
        // 时间判断，如果作业未开始，则无法获取题目
        int code = calculationStatus(homeworkEntity);
        if (code == EvaluationType.HOMEWORK_NO_START.getCode()) {
            return null;
        }
        // 如果作业是考试，计算进入时间
        if (homeworkEntity.getType() == HomeworkTypeEnum.EXAM_HOMEWORK.getCode()) {
            // 超过最迟进入考试时间，无法获取题吗
            if (homeworkEntity.getOpenTime() + homeworkEntity.getLimitTime() * TimeUtils.MINUTE > time && time < homeworkEntity.getCloseTime()) {
                return null;
            }
        }
        return homeworkEntity;
    }

    /**
     * 获取作业下的题目以及用户答案
     * @param homeworkEntity 作业信息
     * @param userId 用户ID
     * @param rightAnswer 是否显示正确答案
     * @param studentName 用户名
     * @param updateSubmit 是更新提交数据,如果是 true，则是用户自己访问，如果是 false 则代表是老师或其它人访问
     * @return 作业模型
     */
    @Override
    public HomeworkModel homeworkQuestionList(HomeworkEntity homeworkEntity,
                                              String userId,
                                              boolean rightAnswer,
                                              String studentName,
                                              boolean updateSubmit) throws JsonProcessingException {
        Map<Long, HomeworkWithQuestionsEntity> questionsMaps =
                homeworkWithQuestionsService.homeworkWithQuestionMap(homeworkEntity.getId());
        if (questionsMaps == null) {
            return null;
        }
        List<QuestionsEntity> questionsEntityList =
                questionsService.listByIds(questionsMaps.keySet());
        if (questionsEntityList == null) {
            return null;
        }

        SubmitHomeworkStatusEntity submitHomeworkStatusEntity =
                submitHomeworkStatusService.findUserSubmit(userId, homeworkEntity.getId());
        // 首次访问
        Map<Long, SubmitQuestionsEntity> submit = submitQuestionsService.findUserSubmit(userId, homeworkEntity);
        if (submitHomeworkStatusEntity == null) {
            submitHomeworkStatusEntity = submitHomeworkStatusService
                    .saveSubmitStatus(homeworkEntity, userId, HomeworkSubmitStatusEnum.TEMPORARY_STORAGE.getCode(), studentName);
            submit = submitQuestionsService.saveQuestions(questionsEntityList, userId, homeworkEntity, questionsMaps);
        } else {
            if (updateSubmit) {
                submitHomeworkStatusEntity.setUpdateTime(System.currentTimeMillis());
                submitHomeworkStatusService.updateById(submitHomeworkStatusEntity);
            }
        }
        // 如果作业已经被老师批改,或者作业已经结束
        // 那么可以直接给出正确答案
        // 如果课程内身份是老师，也可以直接给出答案
        if (submitHomeworkStatusEntity.getStatus() == HomeworkSubmitStatusEnum.COMPLETE.getCode() || homeworkEntity.getCloseTime() <= System.currentTimeMillis()) {
            rightAnswer = true;
        }


        List<QuestionsModel> questionsModels =
                questionsService.questionModelList(
                        homeworkEntity.getId(),
                        userId,
                        questionsEntityList,
                        questionsMaps,
                        submit,
                        rightAnswer
                );

        HomeworkModel homeworkModel = new HomeworkModel();
        BeanUtils.copyProperties(homeworkEntity, homeworkModel);
        homeworkModel.setCloseTime(TimeUtils.formatTime(homeworkEntity.getCloseTime()));
        homeworkModel.setOpenTime(TimeUtils.formatTime(homeworkEntity.getCreateTime()));
        homeworkModel.setStudentId(userId);
        homeworkModel.setStudentName(studentName);
        homeworkModel.setTotalScore(homeworkEntity.getScore());
        homeworkModel.setTeacherComment(submitHomeworkStatusEntity.getTeacherComment());
        homeworkModel.setScore(submitHomeworkStatusEntity.getScore());
        homeworkModel.setSubmit(submitTimeJudge(homeworkEntity, submitHomeworkStatusEntity.getCreateTime(), submitHomeworkStatusEntity));
        homeworkModel.setQuestionsModels(questionsModels);
        homeworkModel.setShowTeacherComment(rightAnswer);
        homeworkModel.setIntoTime(submitHomeworkStatusEntity.getCreateTime());
        return homeworkModel;
    }


    @Override
    public HomeworkModel courseQuestionList(Long homeworkId, Claims user, boolean rightAnswer) throws JsonProcessingException {
        HomeworkEntity homeworkEntity = haveGetQuestionListPower(homeworkId);
        if (homeworkEntity == null) {
            return null;
        }
        // 作业开始后和作业结束后都可以获取题目
        // 老师批改完成，且作业结束后学生可以获取这次的题目答案
        // 获取课程 ID
        Long courseId = homeworkEntity.getClassNumber();
        StudentsCurriculumEntity studentsCurriculumEntity =
                studentsCurriculumService.selectStudentByCurriculumId(user.getId(), courseId);
        if (studentsCurriculumEntity != null) {
            return homeworkQuestionList(homeworkEntity, user.getId(), rightAnswer, studentsCurriculumEntity.getStudentName(), true);
        }
        return null;
    }

    @Override
    public HomeworkModel teacherGetStudentAnswer(Long homeworkId, String studentId, String teacherId) throws JsonProcessingException {
        // 判断作业是否存在
        HomeworkEntity homeworkEntity = this.getById(homeworkId);
        if (homeworkEntity == null) {
            return null;
        }
        // 检查权限
        // 必须是这门课的老师，并且学生也必须是这门课的学生
        // 并且作业要是一提交状态
        if (studentsCurriculumService.checkThisCurriculumHaveTeacher(homeworkEntity.getClassNumber(), teacherId, homeworkEntity)
                && judgeUserIsInCourse(homeworkEntity.getClassNumber(), studentId)) {
            SubmitHomeworkStatusEntity userSubmit = submitHomeworkStatusService.findUserSubmit(studentId, homeworkId);
            if (userSubmit == null) {
                return null;
            }
            if (HomeworkSubmitStatusEnum.teacherHaveSeePower(userSubmit.getStatus())) {
                return homeworkQuestionList(homeworkEntity, studentId, true, userSubmit.getStudentName(), false);
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = JsonProcessingException.class)
    public ReturnCodeEnum submitUserHomework(HomeworkAnswer homeworkAnswer, Claims nowLoginUser) throws JsonProcessingException {
        // 鉴权，查看当前用户有没有提交权限
        HomeworkEntity homeworkEntity = this.getById(homeworkAnswer.getHomeworkId());
        if (homeworkEntity == null) {
            return ReturnCodeEnum.NOO_FOUND;
        }
        long time = System.currentTimeMillis();

        if (judgeUserIsInCourse(homeworkEntity.getClassNumber(), nowLoginUser.getId())) {
            // 判断用户作业是否已经提交过
            SubmitHomeworkStatusEntity submitHomeworkStatusEntity =
                    submitHomeworkStatusService.findUserSubmit(nowLoginUser.getId(), homeworkEntity.getId());
            // 如果这个时候获取到的提交数据是null
            // 那么说明用户没有获取过题目，直接返回未读取题目的错误
            if (submitHomeworkStatusEntity == null) {
                return ReturnCodeEnum.NOT_READ_QUESTION;
            } else {
                if (HomeworkSubmitStatusEnum.isSubmit(submitHomeworkStatusEntity.getStatus())) {
                    if (!submitTimeJudge(homeworkEntity, submitHomeworkStatusEntity.getCreateTime(), submitHomeworkStatusEntity)) {
                        return ReturnCodeEnum.MISS_SUBMIT_TIME;
                    }
                    if (HomeworkSubmitStatusEnum.TEMPORARY_STORAGE.getCode() == homeworkAnswer.getType()) {
                        submitHomeworkStatusEntity.setStatus(HomeworkSubmitStatusEnum.TEMPORARY_STORAGE.getCode());
                        submitHomeworkStatusEntity.setUpdateTime(time);
                        submitHomeworkStatusService.updateById(submitHomeworkStatusEntity);
                        submitQuestionsService.updateSaveQuestions(homeworkAnswer, nowLoginUser.getId(), homeworkEntity);
                    } else {
                        // 更新提交
                        double score = submitQuestionsService.updateSubmitJudgeQuestion(homeworkAnswer, nowLoginUser.getId(), homeworkEntity);
                        submitHomeworkStatusEntity.setStatus(HomeworkSubmitStatusEnum.SUBMIT.getCode());
                        submitHomeworkStatusEntity.setUpdateTime(time);
                        submitHomeworkStatusEntity.setScore(score);
                        submitHomeworkStatusEntity.setSubmitTime(time);
                        // 提交数加 1
                        // TODO 消息通知,考虑要不要加这个提交作业的消息通知
                        homeworkSubmitCountAdd(homeworkEntity, nowLoginUser.getId());
                        submitHomeworkStatusService.updateById(submitHomeworkStatusEntity);
                    }
                    return ReturnCodeEnum.SUCCESS;
                } else {
                    // 不能提交
                    return ReturnCodeEnum.CANNOT_SUBMIT_HOMEWORK;
                }
            }
        }
        return ReturnCodeEnum.NOT_SELECT_THIS_COURSE;
    }


    /**
     * 检查是否有教师权限
     *
     * @param homeworkId 作业ID
     * @param user       当前用户
     */
    public MultipleReturnValues checkTeacherPower(Long homeworkId, Claims user) {
        HomeworkEntity homework = this.getById(homeworkId);
        if (homework == null) {
            return null;
        }
        CurriculumEntity curriculum = curriculumService.getById(homework.getClassNumber());
        List<StudentsCurriculumEntity> teacherList
                = studentsCurriculumService.teacherList(curriculum.getId());
        boolean isTeacher = false;
        for (StudentsCurriculumEntity s : teacherList) {
            if (s.getStudentId().equals(user.getId())) {
                isTeacher = true;
            }
        }
        MultipleReturnValues multipleReturnValues = MultipleReturnValues.ok();
        multipleReturnValues.put("teacherList", teacherList);
        multipleReturnValues.put("isTeacher", isTeacher);
        multipleReturnValues.put("homework", homework);
        multipleReturnValues.put("curriculum", curriculum);
        return multipleReturnValues;
    }


    @Override
    public KeeperDashboardViewVo keeperInfo(Long homeworkId, Claims user) {
        KeeperDashboardViewVo keeperDashboard = new KeeperDashboardViewVo();
        keeperDashboard.setShowPower(false);
        MultipleReturnValues multipleReturnValues = checkTeacherPower(homeworkId, user);
        if (multipleReturnValues == null) {
            return keeperDashboard;
        }
        boolean isTeacher = (Boolean) multipleReturnValues.get("isTeacher");
        List<StudentsCurriculumEntity> teacherList = MultipleReturnValues.cast(multipleReturnValues.get("teacherList"));
        HomeworkEntity homework = MultipleReturnValues.cast(multipleReturnValues.get("homework"));
        CurriculumEntity curriculum = MultipleReturnValues.cast(multipleReturnValues.get("curriculum"));
        if (user.getId().equals(homework.getCreateTeacher())
                || user.getId().equals(curriculum.getCreateTeacher())
                || isTeacher) {
            keeperDashboard.setShowPower(true);
            homework.setStatus(calculationStatus(homework));
            keeperDashboard.setHomework(homework);
            int teacherCount = teacherList.size();
            // 设置学生数目
            keeperDashboard.setStudentCount(
                    studentsCurriculumService.count(new QueryWrapper<StudentsCurriculumEntity>().eq("curriculum_id", curriculum.getId()))
                            // 减去教师数目
                            - teacherCount
            );
            keeperDashboard.setTeacherCount(teacherCount);
            // 填充提交作业列表
            List<SubmitHomeworkStatusEntity> submitHomeworkStatusEntities = submitHomeworkStatusService.submitUserList(homeworkId);
            List<SubmitHomeworkStatusEntity> teacherSubmit = new ArrayList<>();
            List<SubmitHomeworkStatusEntity> studentSubmit = new ArrayList<>();
            Map<String, String> teacherMap = teacherList.stream().collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, StudentsCurriculumEntity::getStudentId));

            for (SubmitHomeworkStatusEntity s : submitHomeworkStatusEntities) {
                if (s.getUserId().equals(teacherMap.get(s.getUserId()))) {
                    teacherSubmit.add(s);
                } else {
                    studentSubmit.add(s);
                }
            }
            keeperDashboard.setTeacherSubmitList(teacherSubmit);
            keeperDashboard.setSubmitList(studentSubmit);
        } else {
            keeperDashboard.setShowPower(false);
        }
        return keeperDashboard;
    }

    @Override
    public ReturnCodeEnum updateHomework(HomeworkModel homeworkModel, Claims user) {
        if (homeworkModel.getId() == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        if (homeworkModel.getEvaluation() == null && homeworkModel.getCloseTime() == null && homeworkModel.getOpenTime() == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        HomeworkEntity homework = this.getById(homeworkModel.getId());
        if (homework == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        CurriculumEntity curriculum = curriculumService.getById(homework.getClassNumber());
        List<StudentsCurriculumEntity> teacherList
                = studentsCurriculumService.teacherList(curriculum.getId(), curriculum.getCreateTeacher());
        boolean isTeacher = false;
        for (StudentsCurriculumEntity s : teacherList) {
            if (s.getStudentId().equals(user.getId())) {
                isTeacher = true;
            }
        }
        if (user.getId().equals(homework.getCreateTeacher())
                || user.getId().equals(curriculum.getCreateTeacher())
                || isTeacher) {
            HomeworkEntity entity = new HomeworkEntity();
            entity.setId(homework.getId());
            // entity.setOpenTime(homeworkModel.getOpenTime());
            entity.setCloseTime(TimeUtils.parseTime(homeworkModel.getCloseTime()));
            entity.setEvaluation(homeworkModel.getEvaluation());
            this.updateById(entity);
            return ReturnCodeEnum.SUCCESS;
        }
        return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
    }

    @Override
    @Transactional(rollbackFor = {})
    public ReturnCodeEnum teacherCorrect(TeacherCommentHomeworkData teacherCommentHomeworkData, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        // 判断学生是否已经提交作业
        SubmitHomeworkStatusEntity userSubmitHomework
                = submitHomeworkStatusService.findUserSubmit(teacherCommentHomeworkData.getStudentId(), teacherCommentHomeworkData.getId());
        if (userSubmitHomework == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        if (!HomeworkSubmitStatusEnum.teacherHaveSeePower(userSubmitHomework.getStatus())) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        MultipleReturnValues multipleReturnValues = checkTeacherPower(teacherCommentHomeworkData.getId(), user);
        if (multipleReturnValues == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        boolean isTeacher = (Boolean) multipleReturnValues.get("isTeacher");
        HomeworkEntity homework = MultipleReturnValues.cast(multipleReturnValues.get("homework"));
        CurriculumEntity curriculum = MultipleReturnValues.cast(multipleReturnValues.get("curriculum"));
        if (user.getId().equals(homework.getCreateTeacher())
                || user.getId().equals(curriculum.getCreateTeacher())
                || isTeacher) {
            // 进行作业批改
            List<QuestionComment> questionList = teacherCommentHomeworkData.getQuestionList();
            Map<Long, QuestionComment> questionCommentMap = new HashMap<>();
            if (questionList != null) {
                questionCommentMap = questionList.stream().collect(Collectors.toMap(QuestionComment::getId, q -> q));
            }

            // 分数计算
            double score = 0;
            // 如果是打回，则不需要进行分数计算和判断
            if (teacherCommentHomeworkData.getStatus() == -1) {
                userSubmitHomework.setUpdateTime(System.currentTimeMillis());
                userSubmitHomework.setStatus(teacherCommentHomeworkData.getStatus());
                userSubmitHomework.setTeacherComment(teacherCommentHomeworkData.getComment());
                // 消息通知
                submitHomeworkStatusService.updateById(userSubmitHomework);
                notificationService.send(user.getId(),
                        user.getSubject(),
                        userSubmitHomework.getUserId(),
                        NotificationTypeEnum.COURSE_KEEPER_ERROR,
                        "你在课程：" + curriculum.getCurriculumName() + " 的作业因为不符合要求，被教师打回，请及时查看！",
                        "/course/learn/" + curriculum.getId() + "/exam/homework/" + homework.getId(),
                        homework.getId());
                return ReturnCodeEnum.SUCCESS;
            }
            // 1. 获取当前作业学生提交数据
            Map<Long, SubmitQuestionsEntity> userSubmit
                    = submitQuestionsService.findUserSubmit(teacherCommentHomeworkData.getStudentId(), homework);
            // 2. 遍历问题数据
            for (SubmitQuestionsEntity submitQuestionsEntity : userSubmit.values()) {
                QuestionComment questionComment = questionCommentMap.get(submitQuestionsEntity.getQuestionId());
                // 问答 填空分数写入
                if (QuestionTypeEnum.noChoice(submitQuestionsEntity.getType())) {
                    if (questionComment == null) {
                        return ReturnCodeEnum.NO_SCORE_DATA;
                    }
                    if (questionComment.getScore() < 0 || questionComment.getScore() > submitQuestionsEntity.getMaxScore()) {
                        return ReturnCodeEnum.NO_SCORE_DATA;
                    }
                    score += questionComment.getScore();
                    submitQuestionsEntity.setScore(questionComment.getScore());
                    submitQuestionsEntity.setTeacherComment(questionComment.getText());
                    // 其它题目分数重新判断
                } else {
                    if (questionComment != null && questionComment.getScore() != null) {
                        if (questionComment.getScore() > 0 && questionComment.getScore() <= submitQuestionsEntity.getMaxScore()) {
                            submitQuestionsEntity.setScore(questionComment.getScore());
                        }
                        submitQuestionsEntity.setTeacherComment(questionComment.getText());
                    }
                    score += submitQuestionsEntity.getScore();
                }
            }
            // 保存批改数据
            submitQuestionsService.updateBatchById(userSubmit.values());
            userSubmitHomework.setUpdateTime(System.currentTimeMillis());
            userSubmitHomework.setScore(score);
            userSubmitHomework.setStatus(teacherCommentHomeworkData.getStatus());
            userSubmitHomework.setTeacherComment(teacherCommentHomeworkData.getComment());
            // 消息通知
            notificationService.send(user.getId(),
                    user.getSubject(),
                    userSubmitHomework.getUserId(),
                    NotificationTypeEnum.COURSE_KEEPER_ERROR,
                    "你在课程：" + curriculum.getCurriculumName() + " 的作业,老师以及批改完成。你现在可以查看你的成绩了！",
                    "/course/learn/" + curriculum.getId() + "/exam/homework/" + homework.getId(),
                    homework.getId());
            submitHomeworkStatusService.updateById(userSubmitHomework);
            return ReturnCodeEnum.SUCCESS;
        }
        return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
    }


    /**
     * 判断用户有没有加入这门课程
     */
    private boolean judgeUserIsInCourse(Long courseId, String userId) {
        StudentsCurriculumEntity studentsCurriculumEntity =
                studentsCurriculumService.selectStudentByCurriculumId(userId, courseId);
        return studentsCurriculumEntity != null;
    }

    /**
     * 计算作业状态
     */
    public int calculationStatus(HomeworkEntity homeworkEntity) {
        long time = System.currentTimeMillis();
        if (homeworkEntity.getOpenTime() <= time && time <= homeworkEntity.getCloseTime()) {
            return EvaluationType.EVALUATION_NO_START.getCode();
        }
        if (time > homeworkEntity.getCloseTime()) {
            // 判断是否开启互评功能
//            if (homeworkEntity.getEvaluation() == EvaluationType.CLOSE_EVALUATION.getCode()) {
//                return EvaluationType.HOMEWORK_END.getCode();
//            } else {
//                return EvaluationType.EVALUATION_START.getCode();
//            }
            // 此处逻辑修改，暂时不需要作业互评状态的判断
            return EvaluationType.HOMEWORK_END.getCode();

        }
        return EvaluationType.HOMEWORK_NO_START.getCode();
    }

    /**
     * 是否有提交权限的判断
     */
    public boolean submitTimeJudge(HomeworkEntity homeworkEntity, long intoTime, SubmitHomeworkStatusEntity submitHomeworkStatusEntity) {
        if (!HomeworkSubmitStatusEnum.isSubmit(submitHomeworkStatusEntity.getStatus())) {
            return false;
        }
        int code = calculationStatus(homeworkEntity);
        if (code == EvaluationType.EVALUATION_NO_START.getCode()) {
            // 如果是测验和考试
            if (homeworkEntity.getType() == HomeworkTypeEnum.TEST_HOMEWORK.getCode() ||
                    homeworkEntity.getType() == HomeworkTypeEnum.EXAM_HOMEWORK.getCode()) {
                return intoTime + (TimeUtils.MINUTE * homeworkEntity.getTime()) >= System.currentTimeMillis();
            } else {
                return true;
            }
        }
        return false;
    }


    public HomeworkWithQuestionsEntity getHomeworkWithQuestionsEntity(long homeworkId, long questionId, String teacher, int score) {
        HomeworkWithQuestionsEntity homeworkWithQuestionsEntity = new HomeworkWithQuestionsEntity();
        homeworkWithQuestionsEntity.setHomeworkId(homeworkId);
        homeworkWithQuestionsEntity.setQuestionId(questionId);
        homeworkWithQuestionsEntity.setCreateTeacher(teacher);
        homeworkWithQuestionsEntity.setCreateTime(System.currentTimeMillis());
        homeworkWithQuestionsEntity.setScore(score);
        return homeworkWithQuestionsEntity;
    }


    /**
     * 作业数据有效性检验
     *
     * @param homeworkModel 作业数据
     *                      不返回结果，直接抛出异常，由 EvaluationControllerAdvice 处理
     */
    public HomeworkEntity homeworkHandleValid(HomeworkModel homeworkModel) {
        // 开始结束时间校验
        Long openTime = TimeUtils.parseTime(homeworkModel.getOpenTime());
        Long closeTime = TimeUtils.parseTime(homeworkModel.getCloseTime());
        if (openTime > closeTime || closeTime < System.currentTimeMillis()) {
            throw new UserDataFormatException("作业时间设置错误!");
        }
        // 如果是测验
        if (HomeworkTypeEnum.TEST_HOMEWORK.getCode() == homeworkModel.getType()) {
            if (homeworkModel.getTime() == null || homeworkModel.getTime() <= 0) {
                throw new UserDataFormatException("提交的测验时间有误，测验时间必须大于 0");
            }
            // 如果是考试
        } else if (HomeworkTypeEnum.EXAM_HOMEWORK.getCode() == homeworkModel.getType()) {
            if (homeworkModel.getLimitTime() == null || homeworkModel.getLimitTime() <= 0) {
                throw new UserDataFormatException("限制进入时间有误，限制进入时间必须大于 0");
            }
            if (homeworkModel.getLimitTime() * TimeUtils.MINUTE + openTime < closeTime) {
                throw new UserDataFormatException("限制进入时间有误，限制进入时间不能超过结束时间。");
            }
            if (homeworkModel.getTime() == null || homeworkModel.getTime() <= 0) {
                homeworkModel.setTime(0);
            }
        }
        HomeworkEntity homeworkEntity = new HomeworkEntity();
        BeanUtils.copyProperties(homeworkModel, homeworkEntity);
        homeworkEntity.setOpenTime(openTime);
        homeworkEntity.setCloseTime(closeTime);
        return homeworkEntity;
    }


    /**
     * 作业提交数加一判断
     * <p>
     * 教师提交不计入提交数
     *
     * @param homeworkEntity 作业类型
     * @param userId         提交用户ID
     */
    private void homeworkSubmitCountAdd(HomeworkEntity homeworkEntity, String userId) {
        if (homeworkEntity.getCreateTeacher().equals(userId)) {
            return;
        }
        List<StudentsCurriculumEntity> list = studentsCurriculumService.teacherList(homeworkEntity.getClassNumber());
        list.forEach(t -> {
            if (t.getStudentId().equals(userId)) {
                return;
            }
        });
        // 提交数加 1;
        // TODO 修改加 1 方式
        homeworkEntity.setSubmitCount(homeworkEntity.getSubmitCount() + 1);
        this.updateById(homeworkEntity);
    }
}
