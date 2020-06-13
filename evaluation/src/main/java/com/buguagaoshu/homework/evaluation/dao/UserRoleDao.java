package com.buguagaoshu.homework.evaluation.dao;

import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Mapper
public interface UserRoleDao extends BaseMapper<UserRoleEntity> {
    /**
     * 查询当前用户列表中用户所拥有的权限
     * @param userEntityList 用户列表
     * @return 权限列表
     * */
	List<UserRoleEntity> selectRoleByUserList(@Param("userEntityList") List<UserEntity> userEntityList);


    UserRoleEntity selectRoleByUserId(@Param("userId") String userId);
}
