package com.buguagaoshu.homework.evaluation.dao;

import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

}
