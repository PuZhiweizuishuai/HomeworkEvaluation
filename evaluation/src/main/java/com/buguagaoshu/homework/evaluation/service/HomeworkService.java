package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.model.HomeworkAnswer;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.vo.KeeperDashboardViewVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;

/**
 * 作业表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface HomeworkService extends IService<HomeworkEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 添加作业
     *
     * @param homeworkModel 作业
     * @param nowLoginUser  现在操作的用户
     * @return 结果
     */
    HomeworkModel add(HomeworkModel homeworkModel, Claims nowLoginUser);


    /**
     * 查找当前课程作业列表
     *
     * @param courseId 课程 ID
     * @param userId   用户 ID
     * @return 课程列表
     */
    List<HomeworkEntity> courseHomeworkList(long courseId, String userId);


    /**
     * 获取当前作业下的题目列表
     *
     * @param homeworkId 作业 ID
     * @param userId   用户 ID
     * @param rightAnswer 是否给出参考答案
     * @return 题目列表
     * @throws JsonProcessingException json 序列化异常
     */
    HomeworkModel courseQuestionList(Long homeworkId, String userId, boolean rightAnswer) throws JsonProcessingException;



    /**
     * 教师获取学生在这个作业下提交的作业答案
     *
     * @param homework 作业ID
     * @param studentId 学生学号
     * @param teacherId 教师ID
     * @throws JsonProcessingException Json 序列化异常
     * @return 该学生提交的作业数据
     * */
    HomeworkModel teacherGetStudentAnswer(Long homework, String studentId, String teacherId) throws JsonProcessingException;


    /**
     * 用户提交作业
     *
     * @param homeworkAnswer 提交的作业数据
     * @param nowLoginUser 当前登陆用户
     * @return 提交结果
     * @throws JsonProcessingException json 序列化异常
     * */
    ReturnCodeEnum submitUserHomework(HomeworkAnswer homeworkAnswer, Claims nowLoginUser) throws JsonProcessingException;



    /**
     * 教师在进行评价与批改时的主页所需要显示的当前作业数据
     * @param homeworkId 作业ID
     * @param user 当前用户
     * @return 数据
     * */
    KeeperDashboardViewVo keeperInfo(Long homeworkId, Claims user);


    /**
     * 更新作业数据
     * @param homeworkEntity 作业ID
     * @param user 当前用户
     * @return 更新结果
     * */
    ReturnCodeEnum updateHomework(HomeworkEntity homeworkEntity, Claims user);
}

