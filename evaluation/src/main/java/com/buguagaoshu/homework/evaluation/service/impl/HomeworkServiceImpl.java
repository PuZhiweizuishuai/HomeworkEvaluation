package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.*;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.HomeworkAnswer;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import com.buguagaoshu.homework.evaluation.vo.KeeperDashboardViewVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        boolean power = false;
        // 检查有无权限进行创建作业
        CurriculumEntity curriculumEntity = curriculumService.getById(homeworkModel.getClassNumber());
        if (!nowLoginUser.getId().equals(curriculumEntity.getCreateTeacher())) {
            // 查询是否拥有班级教师权限
            StudentsCurriculumEntity studentsCurriculumEntity =
                    studentsCurriculumService.selectStudentByCurriculumId(nowLoginUser.getId(), curriculumEntity.getId());
            if (studentsCurriculumEntity == null) {
                return null;
            } else {
                power = true;
            }
        }
        // 拥有创建作业的权限，开始创建作业
        if (nowLoginUser.getId().equals(curriculumEntity.getCreateTeacher()) || power) {
            // 数据校验
            homeworkHandleValid(homeworkModel);

            HomeworkEntity homeworkEntity = new HomeworkEntity();
            BeanUtils.copyProperties(homeworkModel, homeworkEntity);
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
                        questionHandleValid(questionsModel);
                        try {
                            QuestionsEntity questionsEntity = copyQuestionModeToEntity(questionsModel, nowLoginUser.getId());
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
            // TODO 通知班级成员
            homeworkModel.setId(homeworkEntity.getId());
            return homeworkModel;
        }
        return null;
    }

    @Override
    public List<HomeworkEntity> courseHomeworkList(long courseId, String userId) {
        if (!judgeUserIsInCourse(courseId, userId)) {
            return null;
        }
        QueryWrapper<HomeworkEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("class_number", courseId);
        wrapper.orderByDesc("create_time");
        List<HomeworkEntity> list =
                this.list(wrapper);
        if (list == null || list.size() == 0) {
            return new ArrayList<HomeworkEntity>();
        }
        list.forEach((l) -> {
            l.setStatus(calculationStatus(l));
        });
        return list;
    }

    /**
     * 学生是否有获取题目的权限
     * @param homeworkId 作业ID
     * */
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
     * */
    public HomeworkModel homeworkQuestionList(HomeworkEntity homeworkEntity, String userId, boolean rightAnswer) throws JsonProcessingException {
        long time = System.currentTimeMillis();

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
                    .saveSubmitStatus(homeworkEntity, userId, HomeworkSubmitStatusEnum.TEMPORARY_STORAGE.getCode());
            submit = submitQuestionsService.saveQuestions(questionsEntityList, userId, homeworkEntity);
        } else {
            submitHomeworkStatusEntity.setUpdateTime(System.currentTimeMillis());
            submitHomeworkStatusService.updateById(submitHomeworkStatusEntity);
        }
        // 如果作业已经被老师批改，并且作业已经结束
        // 那么可以直接给出正确答案
        // 如果课程内身份是老师，也可以直接给出答案
        if (submitHomeworkStatusEntity.getStatus() == HomeworkSubmitStatusEnum.COMPLETE.getCode()
                && time < homeworkEntity.getCloseTime()) {
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
        homeworkModel.setTotalScore(homeworkEntity.getScore());
        homeworkModel.setScore(submitHomeworkStatusEntity.getScore());
        homeworkModel.setSubmit(submitTimeJudge(homeworkEntity, submitHomeworkStatusEntity.getCreateTime(), submitHomeworkStatusEntity));
        homeworkModel.setQuestionsModels(questionsModels);
        homeworkModel.setIntoTime(submitHomeworkStatusEntity.getCreateTime());
        return homeworkModel;
    }

    @Override
    public HomeworkModel courseQuestionList(Long homeworkId, String userId, boolean rightAnswer) throws JsonProcessingException {
        HomeworkEntity homeworkEntity = haveGetQuestionListPower(homeworkId);
        if (homeworkEntity == null) {
            return null;
        }
        // 作业开始后和作业结束后都可以获取题目
        // 老师批改完成，且作业结束后学生可以获取这次的题目答案
        // 获取课程 ID
        Long courseId = homeworkEntity.getClassNumber();
        if (judgeUserIsInCourse(courseId, userId)) {
            return homeworkQuestionList(homeworkEntity, userId, rightAnswer);
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
                return homeworkQuestionList(homeworkEntity, studentId, true);
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
                        // 提交数加 1
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


    @Override
    public KeeperDashboardViewVo keeperInfo(Long homeworkId, Claims user) {
        KeeperDashboardViewVo keeperDashboard = new KeeperDashboardViewVo();
        keeperDashboard.setShowPower(false);
        HomeworkEntity homework = this.getById(homeworkId);
        if (homework == null) {
            return keeperDashboard;
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
    public ReturnCodeEnum updateHomework(HomeworkEntity homeworkEntity, Claims user) {
        if (homeworkEntity.getId() == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        if (homeworkEntity.getEvaluation() == null && homeworkEntity.getCloseTime() == null && homeworkEntity.getOpenTime() == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        HomeworkEntity homework = this.getById(homeworkEntity.getId());
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
            entity.setOpenTime(homeworkEntity.getOpenTime());
            entity.setCloseTime(homeworkEntity.getCloseTime());
            entity.setEvaluation(homeworkEntity.getEvaluation());
            this.updateById(entity);
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
            if (homeworkEntity.getEvaluation() == EvaluationType.CLOSE_EVALUATION.getCode()) {
                return EvaluationType.HOMEWORK_END.getCode();
            } else {
                return EvaluationType.EVALUATION_START.getCode();
            }

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
     * 将提交的数据拷贝到需要保存到数据库的问题数据
     */
    public QuestionsEntity copyQuestionModeToEntity(QuestionsModel questionsModel, String teacher) throws JsonProcessingException {
        QuestionsEntity questionsEntity = new QuestionsEntity();
        questionsEntity.setQuestion(questionsModel.getQuestion());
        questionsEntity.setCreateTeacher(teacher);
        questionsEntity.setCreateTime(System.currentTimeMillis());
        questionsEntity.setShareStatus(questionsModel.getShareStatus());
        questionsEntity.setTips(questionsModel.getTips());
        questionsEntity.setType(questionsModel.getType());
        ObjectMapper objectMapper = new ObjectMapper();
        if (questionsModel.getType() == QuestionTypeEnum.MULTIPLE_CHOICE.getCode()
                || questionsModel.getType() == QuestionTypeEnum.SINGLE_CHOICE.getCode()) {
            questionsEntity.setAnswer(objectMapper.writeValueAsString(questionsModel.getAnswer()));
            questionsEntity.setOptions(objectMapper.writeValueAsString(questionsModel.getOptions()));
        } else {
            questionsEntity.setOtherAnswer(questionsModel.getOtherAnswer());
        }
        return questionsEntity;
    }


    /**
     * 作业数据有效性检验
     *
     * @param homeworkModel 作业数据
     *                      不返回结果，直接抛出异常，由 EvaluationControllerAdvice 处理
     */
    public void homeworkHandleValid(HomeworkModel homeworkModel) {
        // 开始结束时间校验
        if (homeworkModel.getOpenTime() > homeworkModel.getCloseTime() || homeworkModel.getCloseTime() < System.currentTimeMillis()) {
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
            if (homeworkModel.getTime() == null || homeworkModel.getTime() <= 0) {
                homeworkModel.setTime(0);
            }
        }

    }

    /**
     * 对于题目数据的校验
     *
     * @param questionsModel 题目数据
     */
    public void questionHandleValid(QuestionsModel questionsModel) {
        // 单选和多选题目处理
        if (QuestionTypeEnum.SINGLE_CHOICE.getCode() == questionsModel.getType() ||
                QuestionTypeEnum.MULTIPLE_CHOICE.getCode() == questionsModel.getType()) {
            if (questionsModel.getOptions() == null || questionsModel.getOptions().size() == 0) {
                throw new UserDataFormatException("选择题题目选项设置错误！");
            }
            if (questionsModel.getAnswer() == null || questionsModel.getAnswer().size() == 0) {
                throw new UserDataFormatException("选择题题目答案设置错误！");
            }
            // 判断题答案处理
        } else if (QuestionTypeEnum.JUDGE.getCode() == questionsModel.getType()) {
            try {
                int ans = Integer.parseInt(questionsModel.getOtherAnswer());
                if (ans != QuestionTypeEnum.JUDGE_RIGHT.getCode() && ans != QuestionTypeEnum.JUDGE_ERROR.getCode()) {
                    throw new UserDataFormatException("判断题答案设置错误！");
                }
            } catch (Exception e) {
                throw new UserDataFormatException("判断题答案设置错误！");
            }
        }
        // 分数检查
        if (questionsModel.getScore() == null || questionsModel.getScore() <= 0) {
            throw new UserDataFormatException("题目分数必须大于 0");
        }
    }

    /**
     * 作业提交数加一判断
     *
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
