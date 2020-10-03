package com.buguagaoshu.homework.evaluation.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buguagaoshu.homework.evaluation.entity.InviteCodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 邀请码
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Mapper
public interface InviteCodeDao extends BaseMapper<InviteCodeEntity> {
	void addCount(@Param("col") String col, @Param("id") Long id, @Param("count") Integer count);
}
