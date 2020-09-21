package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.buguagaoshu.homework.evaluation.vo.UserRoleInClassVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 角色表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface UserRoleService extends IService<UserRoleEntity> {

    IPage<UserRoleEntity> queryPage(Map<String, Object> params, String role);

    UserRoleEntity selectByUserId(String userId);

    /**
     * 修改用户角色
     *
     * @param userRoleEntity 修改的角色
     * @param request        获取当前用户
     * @return 处理结果
     */
    ReturnCodeEnum alterUserRole(UserRoleEntity userRoleEntity, HttpServletRequest request);

    /**
     * 教师修改课程内的用户角色
     *
     * @param userRoleInClassVo 修改的角色
     * @param request        获取当前用户
     * @return 处理结果
     */
    ReturnCodeEnum teacherAlterUserRole(UserRoleInClassVo userRoleInClassVo, HttpServletRequest request);
}

