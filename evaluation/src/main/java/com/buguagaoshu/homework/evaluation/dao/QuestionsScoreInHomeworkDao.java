package com.buguagaoshu.homework.evaluation.dao;

import com.buguagaoshu.homework.evaluation.entity.QuestionsScoreInHomeworkEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 负责关联每次作业中每道题目的分值
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-14 20:42:02
 */
@Mapper
public interface QuestionsScoreInHomeworkDao extends BaseMapper<QuestionsScoreInHomeworkEntity> {
	
}
