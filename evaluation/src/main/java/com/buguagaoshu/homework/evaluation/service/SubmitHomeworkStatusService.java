package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitHomeworkStatusEntity;

import java.util.List;
import java.util.Map;

/**
 * 用户作业提交状态
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-16 18:47:06
 */
public interface SubmitHomeworkStatusService extends IService<SubmitHomeworkStatusEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查找用户作业提交状态
     *
     * @param userId 用户ID
     * @param homeworkId 作业ID
     * @return 提交结果
     * */
    SubmitHomeworkStatusEntity findUserSubmit(String userId, Long homeworkId);


    /**
     * 保存提交状态
     * @param homeworkEntity 作业信息
     * @param id 用户ID
     * @param status 状态
     * @param studentName 学生名
     * @return 处理结果
     * */
    SubmitHomeworkStatusEntity saveSubmitStatus(HomeworkEntity homeworkEntity, String id, int status, String studentName);


    /**
     * 获取当前作业下，提交作业的用户列表
     * @param homeworkID 作业ID
     * @return 已提交作业的用户列表
     * */
    List<SubmitHomeworkStatusEntity> submitUserList(Long homeworkID);


    /**
     * 返回未批改的作业列表
     * @param homeworkId  作业ID
     */
    List<SubmitHomeworkStatusEntity> teacherNoCommentSubmit(long homeworkId);


    /**
     * 评论数或喜欢数增长控制
     * */
    void addCount(String col, Long submitId, int count);
}

