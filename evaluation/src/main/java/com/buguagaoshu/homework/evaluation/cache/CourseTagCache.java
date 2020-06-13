package com.buguagaoshu.homework.evaluation.cache;

import com.buguagaoshu.homework.evaluation.entity.CourseTagEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-08 18:58
 * 课程分类缓存信息
 */
@Component
@Data
public class CourseTagCache {
    private List<CourseTagEntity> courseTagListTree;

    private Map<Long, CourseTagEntity> courseTagEntityMap;
}
