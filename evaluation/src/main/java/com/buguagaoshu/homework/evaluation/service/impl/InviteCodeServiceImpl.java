package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.InviteCodeTypeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.dao.InviteCodeDao;
import com.buguagaoshu.homework.evaluation.entity.InviteCodeEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.buguagaoshu.homework.evaluation.service.InviteCodeService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;
import com.buguagaoshu.homework.evaluation.utils.InviteCodeUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


/**
 * @author puzhiwei
 */
@Service("inviteCodeService")
public class InviteCodeServiceImpl extends ServiceImpl<InviteCodeDao, InviteCodeEntity> implements InviteCodeService {

    private final StudentsCurriculumService studentsCurriculumService;

    private final UserRoleService userRoleService;

    @Autowired
    public InviteCodeServiceImpl(StudentsCurriculumService studentsCurriculumService, UserRoleService userRoleService) {
        this.studentsCurriculumService = studentsCurriculumService;
        this.userRoleService = userRoleService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<InviteCodeEntity> page = this.page(
                new Query<InviteCodeEntity>().getPage(params),
                new QueryWrapper<InviteCodeEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public InviteCodeEntity create(InviteCodeEntity inviteCodeEntity, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        // 如果是课程邀请码
        if (inviteCodeEntity.getType().equals(InviteCodeTypeEnum.CURRICULUM.getCode())) {
            StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), inviteCodeEntity.getClassNumber());
            if (studentsCurriculumEntity == null) {
                return null;
            }
            if (studentsCurriculumEntity.getRole().equals(RoleTypeEnum.TEACHER.getRole())) {
                inviteCodeEntity.initData(user.getId());
                this.save(inviteCodeEntity);
                return inviteCodeEntity;
            }
            // 用户邀请码
        } else if (inviteCodeEntity.getType().equals(InviteCodeTypeEnum.USER.getCode())) {
            UserRoleEntity userRoleEntity = userRoleService.selectByUserId(user.getId());
            if (userRoleEntity.getRole().equals(RoleTypeEnum.TEACHER.getRole()) || userRoleEntity.getRole().equals(RoleTypeEnum.ADMIN.getRole())) {
                inviteCodeEntity.initData(user.getId());
                this.save(inviteCodeEntity);
            }
            return null;
        }
        return null;
    }

    @Override
    public PageUtils pageList(Map<String, Object> params, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        UserRoleEntity userRoleEntity = userRoleService.selectByUserId(user.getId());
        QueryWrapper<InviteCodeEntity> wrapper = new QueryWrapper<>();
        String course = (String) params.get("course");
        wrapper.orderByDesc("create_time");
        if (userRoleEntity.getRole().equals(RoleTypeEnum.ADMIN.getRole())) {
            if (!StringUtils.isEmpty(course)) {
                wrapper.eq("class_number", course);
            }
        } else if (userRoleEntity.getRole().equals(RoleTypeEnum.TEACHER.getRole())) {
            if (StringUtils.isEmpty(course)) {
                return null;
            }
            StudentsCurriculumEntity entity = null;
            try {
                entity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), Long.parseLong(course));
            } catch (Exception ignored) { }
            if (entity == null) {
                return null;
            }
            if (!entity.getRole().equals(RoleTypeEnum.TEACHER.getRole())) {
                return null;
            }
            wrapper.eq("class_number", course);
        }
        IPage<InviteCodeEntity> page = this.page(
                new Query<InviteCodeEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

}