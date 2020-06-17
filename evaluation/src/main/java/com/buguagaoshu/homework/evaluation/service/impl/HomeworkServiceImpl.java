package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.*;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.HomeworkAnswer;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
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
                questionsEntityList.forEach((q)-> {
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
        List<HomeworkEntity> list =
                this.list(new QueryWrapper<HomeworkEntity>().eq("class_number", courseId));
        if (list == null || list.size() == 0) {
            return new ArrayList<HomeworkEntity>();
        }
        list.forEach(this::calculationStatus);
        return list;
    }

    @Override
    public HomeworkModel courseQuestionList(Long homeworkId, String userId) throws JsonProcessingException {
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
        // 作业开始后和作业结束后都可以获取题目
        // 获取课程 ID
        Long courseId = homeworkEntity.getClassNumber();
        if (judgeUserIsInCourse(courseId, userId)) {
            List<HomeworkWithQuestionsEntity> homeworkWithQuestionsEntities
                    = homeworkWithQuestionsService
                    .list(new QueryWrapper<HomeworkWithQuestionsEntity>().eq("homework_id", homeworkId));
            if (homeworkWithQuestionsEntities == null) {
                return null;
            }
            Map<Long, HomeworkWithQuestionsEntity> questionsMaps =
                    homeworkWithQuestionsEntities
                    .stream().collect(Collectors.toMap(HomeworkWithQuestionsEntity::getQuestionId, h->h));

            List<QuestionsEntity> questionsEntityList =
                    questionsService.listByIds(questionsMaps.keySet());
            if (questionsEntityList == null) {
                return null;
            }
            SubmitHomeworkStatusEntity submitHomeworkStatusEntity =
                    submitHomeworkStatusService.findUserSubmit(userId, homeworkEntity.getId());
            // 首次访问
            Map<Long ,SubmitQuestionsEntity> submit = submitQuestionsService.findUserSubmit(userId, homeworkEntity);
            if (submitHomeworkStatusEntity == null) {
                submitHomeworkStatusEntity = submitHomeworkStatusService
                        .saveSubmitStatus(homeworkEntity, userId, HomeworkSubmitStatusEnum.TEMPORARY_STORAGE.getCode());
                submit = submitQuestionsService.saveQuestions(questionsEntityList, userId, homeworkEntity);
            } else {
                submitHomeworkStatusEntity.setUpdateTime(System.currentTimeMillis());
                submitHomeworkStatusService.updateById(submitHomeworkStatusEntity);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            List<QuestionsModel> questionsModels = new ArrayList<>();
            for (QuestionsEntity q : questionsEntityList) {
                try {
                    questionsModels.add(questionEntityToModel(q, questionsMaps,  submit, objectMapper));
                } catch (JsonProcessingException e) {
                    log.error("题目反序列化失败，请检查id为 {} 的题目!", q.getId());
                    throw new UserDataFormatException("题目反序列化失败,请稍后重试！");
                }
            }
            HomeworkModel homeworkModel = new HomeworkModel();
            BeanUtils.copyProperties(homeworkEntity, homeworkModel);
            homeworkModel.setQuestionsModels(questionsModels);
            return homeworkModel;
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
                if (!submitTimeJudge(homeworkEntity, submitHomeworkStatusEntity.getCreateTime())) {
                    return ReturnCodeEnum.MISS_SUBMIT_TIME;
                }
                if (HomeworkSubmitStatusEnum.isSubmit(submitHomeworkStatusEntity.getStatus())) {
                    if (HomeworkSubmitStatusEnum.TEMPORARY_STORAGE.getCode() == homeworkAnswer.getType()) {
                        submitHomeworkStatusEntity.setStatus(HomeworkSubmitStatusEnum.TEMPORARY_STORAGE.getCode());
                        submitHomeworkStatusEntity.setUpdateTime(time);
                        submitHomeworkStatusService.updateById(submitHomeworkStatusEntity);
                        submitQuestionsService.updateSaveQuestions(homeworkAnswer, nowLoginUser.getId(), homeworkEntity);
                    } else {
                        // 更新提交
                        double score = submitQuestionsService.updateSubmitJudgeQuestion(homeworkAnswer, nowLoginUser.getId(), homeworkEntity);
                        submitHomeworkStatusEntity.setStatus(HomeworkSubmitStatusEnum.TEMPORARY_STORAGE.getCode());
                        submitHomeworkStatusEntity.setUpdateTime(time);
                        submitHomeworkStatusEntity.setScore(score);
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

    private QuestionsModel questionEntityToModel(QuestionsEntity questionsEntity,
                                                Map<Long, HomeworkWithQuestionsEntity> questionsMaps,
                                                 Map<Long, SubmitQuestionsEntity> submitQuestionsEntityMap,
                                                ObjectMapper objectMapper) throws JsonProcessingException {
        QuestionsModel questionsModel = new QuestionsModel();
        SubmitQuestionsEntity submitQuestionsEntity = submitQuestionsEntityMap.get(questionsEntity.getId());
        BeanUtils.copyProperties(questionsEntity, questionsModel);
        if (QuestionTypeEnum.isChoice(questionsEntity.getType())) {
            questionsModel.setOptions((List<String>) objectMapper.readValue(questionsEntity.getOptions(), List.class));
            questionsModel.setAnswer((List<String>) objectMapper.readValue(submitQuestionsEntity.getAnswer(), List.class));

        }
        questionsModel.setScore(questionsMaps.get(questionsEntity.getId()).getScore());
        questionsModel.setOtherAnswer(submitQuestionsEntity.getOtherAnswer());
        return questionsModel;
    }

    /**
     * 判断用户有没有加入这门课程
     * */
    private boolean judgeUserIsInCourse(Long courseId, String userId) {
        StudentsCurriculumEntity studentsCurriculumEntity =
                studentsCurriculumService.selectStudentByCurriculumId(userId, courseId);
        if (studentsCurriculumEntity == null) {
            return false;
        }
        return true;
    }

    /**
     * 计算作业状态
     * */
    public int calculationStatus(HomeworkEntity homeworkEntity) {
        long time = System.currentTimeMillis();
        if (homeworkEntity.getOpenTime() <= time && time <= homeworkEntity.getCloseTime()) {
            return EvaluationType.EVALUATION_NO_START.getCode();
        }
        if (time > homeworkEntity.getCloseTime()) {
            return EvaluationType.EVALUATION_START.getCode();
        }
        return EvaluationType.HOMEWORK_NO_START.getCode();
    }

    /**
     * 是否有提交权限的判断
     * */
    public boolean submitTimeJudge(HomeworkEntity homeworkEntity, long intoTime) {
        int code = calculationStatus(homeworkEntity);
        if (code == EvaluationType.EVALUATION_NO_START.getCode()) {
            // 如果是测验和考试
            if (homeworkEntity.getType() == HomeworkTypeEnum.TEST_HOMEWORK.getCode() ||
                homeworkEntity.getType() == HomeworkTypeEnum.EXAM_HOMEWORK.getCode()) {
                if (intoTime + TimeUtils.MINUTE * homeworkEntity.getTime() <= System.currentTimeMillis()) {
                    return true;
                }
                return false;
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
     * */
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
        // 分数检擦
        if (questionsModel.getScore() == null || questionsModel.getScore() <= 0) {
            throw new UserDataFormatException("题目分数必须大于 0");
        }
    }



}
