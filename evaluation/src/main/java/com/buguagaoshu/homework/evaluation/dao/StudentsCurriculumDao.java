package com.buguagaoshu.homework.evaluation.dao;

import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生-课程关系列表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Mapper
public interface StudentsCurriculumDao extends BaseMapper<StudentsCurriculumEntity> {
	
}
