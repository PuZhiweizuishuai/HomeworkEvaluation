package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import io.jsonwebtoken.Claims;

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
     * @param homeworkEntity 作业
     * @param nowLoginUser 现在操作的用户
     * @return 结果
     * */
    HomeworkEntity add(HomeworkEntity homeworkEntity, Claims nowLoginUser);
}

