package com.buguagaoshu.homework.evaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buguagaoshu.homework.evaluation.entity.HomeworkWithQuestionsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问题-作业关联表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Mapper
public interface HomeworkWithQuestionsDao extends BaseMapper<HomeworkWithQuestionsEntity> {
	
}
