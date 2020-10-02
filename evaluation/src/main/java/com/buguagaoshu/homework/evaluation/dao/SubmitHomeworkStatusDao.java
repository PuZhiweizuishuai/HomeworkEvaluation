package com.buguagaoshu.homework.evaluation.dao;

import com.buguagaoshu.homework.evaluation.entity.SubmitHomeworkStatusEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户作业提交状态
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-16 18:47:06
 */
@Mapper
public interface SubmitHomeworkStatusDao extends BaseMapper<SubmitHomeworkStatusEntity> {

    void addCount(@Param("col") String col, @Param("submitId") Long submitId, @Param("count") int count);
}
