package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.model.User;
import com.buguagaoshu.homework.evaluation.vo.AdminAddUser;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.vo.AlterUserStatus;
import com.buguagaoshu.homework.evaluation.vo.PasswordVo;
import com.buguagaoshu.homework.evaluation.vo.UserUpdateVo;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 用户表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 负责为管理员页面查询包含角色的用户列表
     * @param params 查询参数
     *               page 当前页码
     *               limit 每页显示数量
     *               username 用户名
     *               userId 学号
     *               role 角色
     * @return 分页结果
     * */
    PageUtils selectUserAndRoleList(Map<String, Object> params);


    /**
     * 管理员添加用户
     * @param userEntityList 用户
     * @param request 获取请求地址
     * @return 插入结果
     * */
    List<AdminAddUser> adminAddUser(List<UserEntity> userEntityList, HttpServletRequest request);


    /**
     * 管理员重置用户密码
     * @param user 需要重置密码的用户
     * @return 重置后的密码
     * */
    AdminAddUser restPassword(AdminAddUser user);

    /**
     * 封锁或禁言用户账号
     * @param alterUserStatus 用户ID，时间，类型
     * @return 处理结果
     * */
    ReturnCodeEnum alterUserStatus(AlterUserStatus alterUserStatus);


    /**
     * 教师获取本班级学生
     * @param params 查询参数
     *               classId: 班级 ID
     *               page： 页码
     *               limit： 每页显示数量
     *               name： 学生名字
     *               nameId： 学生 ID
     *               role: 角色
     * @param nowLoginUser 当前登陆的用户
     * @return 分页后的结果
     * */
    PageUtils selectClassUser(Map<String, Object> params, Claims nowLoginUser);


    /**
     * 添加学生和课程之间的关系
     * @param userList 学生列表
     * @param teacher 操作的老师 ID
     * @return  结果
     * */
    Map<String, String> addStudentCurriculumRelationship(List<AdminAddUser> userList, Claims teacher);


    /**
     * 通过邮箱查找用户
     * */
    UserEntity findByEmail(String s);


    /**
     * 通过用户 ID 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     * */
    User userInfo(String userId);


    /**
     * 更新用户密码
     * */
    ReturnCodeEnum updatePassword(PasswordVo passwordVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新首页大图
     * */
    ReturnCodeEnum updateTopImg(UserUpdateVo userUpdateVo, HttpServletRequest request);

    /**
     * 更新头像
     * */
    ReturnCodeEnum updateAvatar(UserUpdateVo userUpdateVo, HttpServletRequest request);
}

