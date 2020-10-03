package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.InviteCodeTypeEnum;
import com.buguagaoshu.homework.common.enums.NotificationTypeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.dao.InviteCodeDao;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.RegisterUserVo;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


/**
 * @author puzhiwei
 */
@Service("inviteCodeService")
public class InviteCodeServiceImpl extends ServiceImpl<InviteCodeDao, InviteCodeEntity> implements InviteCodeService {

    private StudentsCurriculumService studentsCurriculumService;

    private UserRoleService userRoleService;

    private CurriculumService curriculumService;

    private NotificationService notificationService;

    private final InviteCodeUseLogService inviteCodeUseLogService;

    @Autowired
    public InviteCodeServiceImpl(InviteCodeUseLogService inviteCodeUseLogService) {
        this.inviteCodeUseLogService = inviteCodeUseLogService;
    }

    @Autowired
    public void setStudentsCurriculumService(StudentsCurriculumService studentsCurriculumService) {
        this.studentsCurriculumService = studentsCurriculumService;
    }

    @Autowired
    public void setUserRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @Autowired
    public void setCurriculumService(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
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
            } catch (Exception ignored) {
            }
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

    @Override
    public PageUtils useLog(Map<String, Object> params, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        String codeId = (String) params.get("code");
        if (!StringUtils.isEmpty(codeId)) {
            UserRoleEntity userRoleEntity = userRoleService.selectByUserId(user.getId());
            if (userRoleEntity.getRole().equals(RoleTypeEnum.ADMIN.getRole())) {
                return inviteCodeUseLogService.queryPage(params, codeId);
            }
            InviteCodeEntity inviteCodeEntity = this.getById(codeId);
            if (inviteCodeEntity.getType().equals(InviteCodeTypeEnum.CURRICULUM.getCode())) {
                StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), inviteCodeEntity.getClassNumber());
                if (studentsCurriculumEntity.getRole().equals(RoleTypeEnum.TEACHER.getRole())) {
                    return inviteCodeUseLogService.queryPage(params, codeId);
                }
            }
        }
        throw new UserDataFormatException("查询参数错误！");
    }

    @Override
    @Transactional(rollbackFor = {})
    public void checkCode(RegisterUserVo registerUserVo) {
        QueryWrapper<InviteCodeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("code", registerUserVo.getInvitationCode());
        InviteCodeEntity one = this.getOne(wrapper);
        if (one == null) {
            throw new UserDataFormatException("邀请码错误！");
        }
        long time = System.currentTimeMillis();
        InviteCodeUseLogEntity inviteCodeUseLogEntity = new InviteCodeUseLogEntity();
        inviteCodeUseLogEntity.setInvitecodeId(one.getId());
        inviteCodeUseLogEntity.setStudentId(registerUserVo.getUserId());
        inviteCodeUseLogEntity.setUseTime(time);
        if (one.getUseCount() > 0 && one.getStatus().equals(InviteCodeTypeEnum.AVAILABLE.getCode())) {
            if (one.getExpireTime() != null) {
                if (one.getExpireTime() < time) {
                    throw new UserDataFormatException("邀请码已经过期！");
                }
            }
            this.baseMapper.addCount("use_count", one.getId(), -1);
            inviteCodeUseLogService.save(inviteCodeUseLogEntity);

            if (one.getType().equals(InviteCodeTypeEnum.CURRICULUM.getCode())) {
                CurriculumEntity entity = curriculumService.getById(one.getClassNumber());
                StudentsCurriculumEntity student = new StudentsCurriculumEntity();
                student.setStudentName(registerUserVo.getUsername());
                student.setStudentId(registerUserVo.getUserId());
                student.setRole(RoleTypeEnum.STUDENT.getRole());
                student.setCurriculumId(entity.getId());
                student.setCreateTime(System.currentTimeMillis());
                studentsCurriculumService.save(student);
                notificationService.send(registerUserVo.getUserId(),
                        registerUserVo.getUsername(),
                        entity.getCreateTeacher(),
                        NotificationTypeEnum.COURSE_JOIN,
                        "学生: " + registerUserVo.getUserId() + " " + registerUserVo.getUsername() + "使用邀请码：" + one.getCode() + "，进入了课程！",
                        "/user/" + registerUserVo.getUserId(),
                        entity.getId());
                curriculumService.addCount("student_number", entity.getId(), 1);
            }
        } else {
            throw new UserDataFormatException("邀请码不可用或超过了最大使用次数！");
        }
    }
}