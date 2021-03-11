package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.CoursewareEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-26 21:37:00
 */
public interface CoursewareService extends IService<CoursewareEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 返回课件树
     * @param courseId 课程ID
     * @param request 用户信息
     * @return 课件树
     * */
    List<CoursewareEntity> coursewareTree(Long courseId, HttpServletRequest request);


    /**
     * 保存课件信息
     * @param coursewareEntity 课件信息
     * @param request 获取用户信息
     * @return 结果
     * */
    boolean saveCourseware(CoursewareEntity coursewareEntity, HttpServletRequest request);


    boolean updateCourseware(CoursewareEntity coursewareEntity, HttpServletRequest request);


    boolean delete(CoursewareEntity coursewareEntity, HttpServletRequest request);
}

