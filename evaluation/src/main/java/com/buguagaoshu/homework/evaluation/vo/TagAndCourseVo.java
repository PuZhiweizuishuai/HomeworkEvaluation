package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.evaluation.entity.CourseTagEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-11 22:23
 * 标签结合课程数据
 */
@Data
public class TagAndCourseVo {

    /**
     * 标签标题
     */
    private String title;


    /**
     * 标签 ID
     */
    private Long id;


    /**
     * 标签ICON
     */
    private String icon;

    /**
     * 课程列表
     */
    private List<SimpleCourseVo> courseVos;


    /**
     * 子分类
     */
    private List<CourseTagEntity> courseTagEntityList;
}
