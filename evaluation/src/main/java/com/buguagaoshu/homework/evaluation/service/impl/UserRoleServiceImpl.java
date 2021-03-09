package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.CurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.CurriculumService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.UserRoleInClassVo;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.UserRoleDao;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;

import javax.servlet.http.HttpServletRequest;


/**
 * @author puzhiwei
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleDao, UserRoleEntity> implements UserRoleService {


    private CurriculumService curriculumService;

    private final StudentsCurriculumService studentsCurriculumService;

    @Autowired
    public void setCurriculumService(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @Autowired
    public UserRoleServiceImpl(StudentsCurriculumService studentsCurriculumService) {
        this.studentsCurriculumService = studentsCurriculumService;
    }

    @Override
    public IPage<UserRoleEntity> queryPage(Map<String, Object> params, String role) {
        IPage<UserRoleEntity> page = this.page(
                new Query<UserRoleEntity>().getPage(params),
                new QueryWrapper<UserRoleEntity>().eq("role", role).orderByDesc("create_time")
        );

        return page;
    }

    @Override
    public UserRoleEntity selectByUserId(String userId) {
        return baseMapper.selectRoleByUserId(userId);
    }

    @Override
    public ReturnCodeEnum alterUserRole(UserRoleEntity userRoleEntity, HttpServletRequest request) {
        UserRoleEntity old = this.baseMapper.selectRoleByUserId(userRoleEntity.getUserId());
        if (old == null) {
            return ReturnCodeEnum.USER_NOT_FIND;
        }
        String nowUser = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId();
        if (nowUser.equals(old.getUserId())) {
            return ReturnCodeEnum.CANNOT_BE_ALTER_YOUR_ROLE;
        }
        if (RoleTypeEnum.check(userRoleEntity.getRole())) {
            old.setRole(userRoleEntity.getRole());
            old.setCreateTime(System.currentTimeMillis());

            old.setOperator(nowUser);
            this.updateById(old);
            return ReturnCodeEnum.SUCCESS;
        } else {
            return ReturnCodeEnum.USER_ROLE_BAD;
        }

    }

    @Override
    public ReturnCodeEnum teacherAlterUserRole(UserRoleInClassVo userRoleInClassVo, HttpServletRequest request) {
        Claims teacher = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        if (RoleTypeEnum.check(userRoleInClassVo.getRole(), (String) teacher.get("authorities"))) {
            CurriculumEntity curriculumEntity = curriculumService.getById(userRoleInClassVo.getCourseNumber());
            if (curriculumEntity == null) {
                return ReturnCodeEnum.NOO_FOUND;
            }
            if (teacher.getId().equals(userRoleInClassVo.getUserId())) {
                return ReturnCodeEnum.CANNOT_BE_ALTER_YOUR_ROLE;
            }
            if (curriculumEntity.getCreateTeacher().equals(teacher.getId())) {
                StudentsCurriculumEntity studentsCurriculumEntity =
                        studentsCurriculumService.selectStudentByCurriculumId(userRoleInClassVo.getUserId(),
                                curriculumEntity.getId());
                if (studentsCurriculumEntity == null) {
                    return ReturnCodeEnum.USER_NOT_FIND;
                }
                studentsCurriculumEntity.setRole(userRoleInClassVo.getRole());
                // 更新课程内角色
                studentsCurriculumService.updateById(studentsCurriculumEntity);
                return ReturnCodeEnum.SUCCESS;
            } else {
                return ReturnCodeEnum.NO_ALTER_ROLE_POWER;
            }
        }
        return ReturnCodeEnum.ROLE_TYPE_ERROR;
    }



}
