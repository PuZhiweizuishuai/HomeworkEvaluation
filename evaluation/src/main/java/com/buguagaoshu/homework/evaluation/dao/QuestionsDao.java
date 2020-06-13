package com.buguagaoshu.homework.evaluation.dao;

import com.buguagaoshu.homework.evaluation.entity.QuestionsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问题表，需要与作业表关联
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Mapper
public interface QuestionsDao extends BaseMapper<QuestionsEntity> {
	
}
