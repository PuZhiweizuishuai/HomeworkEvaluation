package com.buguagaoshu.homework.evaluation.dao;

import com.buguagaoshu.homework.evaluation.entity.CurriculumEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 班级表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Mapper
public interface CurriculumDao extends BaseMapper<CurriculumEntity> {
	void addCount(String col, Long id, Integer count);

	void addCount(String col, Long id, Double count);
}
