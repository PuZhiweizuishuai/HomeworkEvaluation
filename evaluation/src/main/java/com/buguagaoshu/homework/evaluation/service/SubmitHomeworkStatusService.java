package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitHomeworkStatusEntity;

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
     * @return 处理结果
     * */
    SubmitHomeworkStatusEntity saveSubmitStatus(HomeworkEntity homeworkEntity, String id, int status);


}

