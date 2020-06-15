package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.model.HomeworkModel;
import com.buguagaoshu.homework.evaluation.model.QuestionsModel;
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
     * @return 题目列表
     */
    List<QuestionsModel> courseQuestionList(Long homeworkId, String userId);
}

