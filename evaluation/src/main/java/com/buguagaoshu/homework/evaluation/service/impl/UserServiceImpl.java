package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.config.CustomConstant;
import com.buguagaoshu.homework.common.domain.CustomPage;
import com.buguagaoshu.homework.common.enums.PasswordStatusEnum;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.common.enums.UserStatusEnum;
import com.buguagaoshu.homework.evaluation.config.BaseWebInfoConfig;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.dao.UserRoleDao;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.User;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.*;
import com.buguagaoshu.homework.common.utils.InviteCodeUtil;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.UserDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * @author puzhiwei
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    public final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserRoleService userRoleService;

    public final UserRoleDao userRoleDao;

    private final StudentsCurriculumService studentsCurriculumService;

    private CurriculumService curriculumService;

    private NotificationService notificationService;

    private final VerifyCodeService verifyCodeService;

    private final BaseWebInfoConfig baseWebInfoConfig;

    private  InviteCodeService inviteCodeService;

    @Autowired
    public void setInviteCodeService(InviteCodeService inviteCodeService) {
        this.inviteCodeService = inviteCodeService;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setCurriculumService(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @Autowired
    public void setUserRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserRoleDao userRoleDao, StudentsCurriculumService studentsCurriculumService, VerifyCodeService verifyCodeService, BaseWebInfoConfig baseWebInfoConfig) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRoleDao = userRoleDao;
        this.studentsCurriculumService = studentsCurriculumService;
        this.verifyCodeService = verifyCodeService;
        this.baseWebInfoConfig = baseWebInfoConfig;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils selectUserAndRoleList(Map<String, Object> params) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        String username = (String) params.get("username");
        // 倒序输出
        wrapper.orderByDesc("create_time");
        if (!StringUtils.isEmpty(username)) {
            wrapper.like("username", username);
        }
        String userId = (String) params.get("userId");

        if (!StringUtils.isEmpty(userId)) {
            wrapper.like("user_id", userId);
        }
        // TODO 用户与角色不在一个表，代优化
        String role = (String) params.get("role");
        if (!StringUtils.isEmpty(role)) {
            IPage<UserRoleEntity> userRoleEntityIPage = userRoleService.queryPage(params, role);
            if (userRoleEntityIPage.getTotal() == 0) {
                return new PageUtils(userRoleEntityIPage);
            }
            Map<String, UserRoleEntity> rolMap = userRoleEntityIPage.getRecords().stream().collect(Collectors.toMap(UserRoleEntity::getUserId, h -> h));

            List<UserEntity> userEntityList = this.listByIds(rolMap.keySet());
            return createUserAndRole(userEntityList, new PageUtils(userRoleEntityIPage), rolMap);

        } else {
            IPage<UserEntity> page = this.page(
                    new Query<UserEntity>().getPage(params),
                    wrapper
            );
            PageUtils pageUtils = new PageUtils(page);
            List<UserEntity> userEntityList = (List<UserEntity>) pageUtils.getList();
            if (userEntityList.size() > 0) {
                return createUserAndRole(userEntityList, pageUtils, null);
            }
            return pageUtils;
        }
    }

    public PageUtils createUserAndRole(List<UserEntity> userEntityList, PageUtils pageUtils, Map<String, UserRoleEntity> roleMap) {
        userEntityList.forEach(this::checkUserStatus);

        if (roleMap == null) {
            List<UserRoleEntity> roleEntityList =
                    userRoleDao.selectRoleByUserList(userEntityList);
            roleMap = roleEntityList.stream().collect(Collectors.toMap(UserRoleEntity::getUserId, h -> h));
        }


        List<UserAndRole> userAndRoleList = new ArrayList<>();

        for (UserEntity userEntity : userEntityList) {
            UserAndRole userAndRole = new UserAndRole();
            BeanUtils.copyProperties(userEntity, userAndRole);
            userAndRole.setRole(roleMap.get(userEntity.getUserId()));
            userAndRole.setCreateTime(TimeUtils.formatTime(userEntity.getCreateTime()));
            userAndRole.setLatestLoginTime(TimeUtils.formatTime(userEntity.getLatestLoginTime()));
            userAndRoleList.add(userAndRole);
        }
        pageUtils.setList(userAndRoleList);
        return pageUtils;
    }


    @Override
    @Transactional(rollbackFor = {})
    public List<AdminAddUser> adminAddUser(List<UserEntity> userEntityList, HttpServletRequest request) {
        // 保存需要返回的账号，密码
        List<AdminAddUser> adminAddUserList = new ArrayList<>();
        // 保存有效的用户 ID
        List<UserEntity> validUser = new ArrayList<>();
        for (UserEntity u : userEntityList) {
            AdminAddUser adminAddUserFound = new AdminAddUser();
            // 判断 ID 是否符合要求，符合要求的在去重
            if (!StringUtils.isEmpty(u.getUserId()) && u.getUserId().length() < 20) {
                UserEntity user = this.getById(u.getUserId());
                if (user != null) {
                    adminAddUserFound.setUserId(u.getUserId());
                    adminAddUserFound.setRole(user.getRole());
                    adminAddUserFound.setStatus(ReturnCodeEnum.USER_ALREADY_EXISTS.getCode());
                    adminAddUserFound.setMsg(ReturnCodeEnum.USER_ALREADY_EXISTS.getMsg());
                    adminAddUserList.add(adminAddUserFound);
                } else {
                    if (RoleTypeEnum.check(u.getRole())) {
                        // 保存有效的ID
                        validUser.add(u);
                    } else {
                        adminAddUserFound.setUserId(u.getUserId());
                        adminAddUserFound.setRole(u.getRole());
                        adminAddUserFound.setStatus(ReturnCodeEnum.USER_ROLE_BAD.getCode());
                        adminAddUserFound.setMsg(ReturnCodeEnum.USER_ROLE_BAD.getMsg());
                        adminAddUserList.add(adminAddUserFound);
                    }
                }
            } else {
                adminAddUserFound.setUserId(u.getUserId());
                adminAddUserFound.setRole(u.getRole());
                adminAddUserFound.setStatus(ReturnCodeEnum.USER_ID_BAD.getCode());
                adminAddUserFound.setMsg(ReturnCodeEnum.USER_ID_BAD.getMsg());
                adminAddUserList.add(adminAddUserFound);
            }
        }

        // 保存要插入的用户权限
        List<UserRoleEntity> userRoleEntityList = new ArrayList<>();

        for (UserEntity userEntity : validUser) {
            AdminAddUser addUser = new AdminAddUser();
            String password = InviteCodeUtil.createInviteCode().substring(0, 6);
            // 自动填充时间IP等参数
            initUser(userEntity, request);
            userEntity.setPassword(bCryptPasswordEncoder.encode(password));
            // 设置权限
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            // 填充当前管理员账号
            String adminId = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId();

            userRoleEntity.setOperator(adminId);

            userRoleEntity.setUserId(userEntity.getUserId());
            userRoleEntity.setRole(userEntity.getRole());
            userRoleEntity.setCreateTime(System.currentTimeMillis());
            userRoleEntityList.add(userRoleEntity);
            addUser.setUserId(userEntity.getUserId());
            addUser.setPassword(password);
            addUser.setStatus(ReturnCodeEnum.SUCCESS.getCode());
            addUser.setMsg(ReturnCodeEnum.SUCCESS.getMsg());
            addUser.setRole(userEntity.getRole());
            adminAddUserList.add(addUser);
        }
        this.saveBatch(validUser);
        userRoleService.saveBatch(userRoleEntityList);
        return adminAddUserList;
    }

    @Override
    public AdminAddUser restPassword(AdminAddUser user) {
        UserEntity userEntity = this.getById(user.getUserId());
        AdminAddUser adminAddUser = new AdminAddUser();
        if (userEntity != null) {
            String newPassword = InviteCodeUtil.createInviteCode().substring(0, 6);
            userEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userEntity.setResetPasswordStatus(PasswordStatusEnum.RESET_PASSWORD.getCode());
            this.updateById(userEntity);

            adminAddUser.setPassword(newPassword);
            adminAddUser.setUserId(userEntity.getUserId());
            adminAddUser.setStatus(ReturnCodeEnum.SUCCESS.getCode());
            return adminAddUser;
        }
        adminAddUser.setStatus(ReturnCodeEnum.USER_NOT_FIND.getCode());
        adminAddUser.setUserId(user.getUserId());
        return adminAddUser;
    }


    @Override
    public ReturnCodeEnum alterUserStatus(AlterUserStatus alterUserStatus) {
        if (UserStatusEnum.check(alterUserStatus.getStatus())
                && alterUserStatus.getTime() != null && alterUserStatus.getTime() > 0) {
            UserEntity userEntity = this.getById(alterUserStatus.getUserId());
            if (userEntity != null) {
                userEntity.setStartLockTime(System.currentTimeMillis());
                userEntity.setLockTime(alterUserStatus.getTime() * 60000);
                if (alterUserStatus.getStatus().equals(UserStatusEnum.NORMAL.getCode())) {
                    userEntity.setStartLockTime(0L);
                    userEntity.setLockTime(0L);
                }
                userEntity.setStatus(alterUserStatus.getStatus());
                this.updateById(userEntity);
                return ReturnCodeEnum.SUCCESS;
            } else {
                return ReturnCodeEnum.USER_NOT_FIND;
            }
        } else {
            return ReturnCodeEnum.USER_LOCK_TYPE_BAD;
        }
    }

    @Override
    public PageUtils selectClassUser(Map<String, Object> params, Claims nowLoginUser) {
        String courseId = (String) params.get("classId");
        if (StringUtils.isEmpty(courseId)) {
            return null;
        }
        CurriculumEntity curriculumEntity = curriculumService.getById(courseId);
        if (curriculumEntity == null) {
            return null;
        }
        if (studentsCurriculumService.checkThisCurriculumHaveTeacher(curriculumEntity.getId(), nowLoginUser.getId())) {
            String name = (String) params.get("name");
            String userId = (String) params.get("nameId");
            String role = (String) params.get("role");
            QueryWrapper<StudentsCurriculumEntity> wrapper = new QueryWrapper<>();
            if (!StringUtils.isEmpty(name)) {
                wrapper.like("student_name", name);
            }
            if (!StringUtils.isEmpty(userId)) {
                wrapper.like("student_id", userId);
            }
            if (!StringUtils.isEmpty(role)) {
                wrapper.eq("role", role);
            }
            wrapper.eq("curriculum_id", courseId);
            IPage<StudentsCurriculumEntity> page = studentsCurriculumService.page(
                    new Query<StudentsCurriculumEntity>().getPage(params),
                    wrapper
            );
            if (page.getTotal() == 0) {
                return null;
            }
            Map<String, StudentsCurriculumEntity> rolesMap =
                    page.getRecords()
                            .stream()
                            .collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, u -> u));
            List<UserEntity> userEntities = this.listByIds(rolesMap.keySet());
            List<UserAndRole> userAndRoles = addRoleAndCleanPassword(userEntities, nowLoginUser.getId(), rolesMap);
            return new PageUtils(new CustomPage<>(userAndRoles, page.getTotal(), page.getSize(), page.getCurrent(), page.orders()));

        } else {
            return  null;
        }
    }

    @Override
    @Transactional(rollbackFor = {})
    public Map<String, String> addStudentCurriculumRelationship(List<AdminAddUser> userList, Claims teacher) {
        Map<String, String> map = new HashMap<>(16);
        if (userList != null && userList.size() != 0) {
            // 获取要添加的课程号
            Long courseNumber = userList.get(0).getCourseNumber();
            CurriculumEntity curriculumEntity = curriculumService.getById(courseNumber);
            if (courseNumber == null) {
                return null;
            }
            // 记录学生人数
            AtomicInteger count = new AtomicInteger(0);
            // 判断当前用户是否有权限进行此操作
            if (checkRole(curriculumEntity, teacher)) {
                // 获取上传学生信息
                List<UserEntity> userEntities = this.listByIds(userList.stream().map(AdminAddUser::getUserId).collect(Collectors.toList()));
                if (userEntities == null) {
                    return null;
                }
                Map<String, UserEntity> userEntityMap = userEntities.stream().collect(Collectors.toMap(UserEntity::getUserId, u -> u));
                // 查找当前课程学生列表，避免重复加入
                List<StudentsCurriculumEntity> studentsCurriculumEntities =
                        studentsCurriculumService.list(new QueryWrapper<StudentsCurriculumEntity>().eq("curriculum_id", courseNumber));
                // 直接导入
                Map<String, StudentsCurriculumEntity> stringStudentsCurriculumEntityMap
                        = studentsCurriculumEntities.stream().collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, u -> u));
                List<StudentsCurriculumEntity> students = new ArrayList<>();

                // 构造要导入学生列表
                classImportUser(userList, map, stringStudentsCurriculumEntityMap, userEntityMap, students, count, courseNumber);
                // 保存学生列表

                studentsCurriculumService.saveBatch(students);
                // 给学生发送通知
                notificationService.sendJoinCourseToUser(students, teacher.getId(), teacher.getSubject(), curriculumEntity);
                // 保存课程人数
                curriculumEntity.setStudentNumber(count.get() + curriculumEntity.getStudentNumber());

            } else {
                return null;
            }
            curriculumService.addCount("student_number", curriculumEntity.getId(), count.get());
            return map;
        }
        return null;
    }

    @Override
    public UserEntity findByEmail(String s) {
        return getOne(new QueryWrapper<UserEntity>().eq("email", s));
    }

    @Override
    public UserEntity findByPhoneNumber(String s) {
        return getOne(new QueryWrapper<UserEntity>().eq("phone_number", s));
    }

    @Override
    public User userInfo(String userId) {
        UserEntity userEntity = this.getById(userId);
        if (userEntity == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        user.setPassword(null);
        user.setLatestLoginIp(null);
        if (userEntity.getUserQqStatus() == 0) {
            user.setUserQq(null);
        }
        if (userEntity.getUserWechatStatus() == 0) {
            user.setUserWechat(null);
        }
        if (userEntity.getUserEmailStatus() == 0) {
            user.setEmail(null);
        }
        if (userEntity.getUserPhoneStatus() == 0) {
            user.setPhoneNumber(null);
        }
        UserRoleEntity userRoleEntity = userRoleService.selectByUserId(userId);
        user.setRole(userRoleEntity);
        return user;
    }

    @Override
    public ReturnCodeEnum updatePassword(PasswordVo passwordVo, HttpServletRequest request, HttpServletResponse response) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        HttpSession session = request.getSession();
        String verifyCodeKey = (String) session.getAttribute(WebConstant.VERIFY_CODE_KEY);
        verifyCodeService.verify(verifyCodeKey, passwordVo.getVerifyCode());
        UserEntity userEntity = getById(user.getId());
        if (userEntity == null) {
            return ReturnCodeEnum.USER_NOT_FIND;
        }
        if (bCryptPasswordEncoder.matches(passwordVo.getOldPassword(), userEntity.getPassword())) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(passwordVo.getNewPassword()));
            this.updateById(userEntity);
            Cookie cookie = WebUtils.getCookie(request, TokenAuthenticationHelper.COOKIE_TOKEN);
            cookie.setValue(null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return ReturnCodeEnum.SUCCESS;
        } else {
            throw new UserDataFormatException("原密码错误！");
        }
    }

    @Override
    public ReturnCodeEnum updateTopImg(UserUpdateVo userUpdateVo, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        UserEntity entity = getById(user.getId());
        if (entity == null) {
            return null;
        }
        if (!StringUtils.isEmpty(userUpdateVo.getTopImgUrl())) {
            entity.setTopImgUrl(userUpdateVo.getTopImgUrl());
            this.updateById(entity);
            return ReturnCodeEnum.SUCCESS;
        }
        return ReturnCodeEnum.DATA_VALID_EXCEPTION;
    }

    @Override
    public ReturnCodeEnum updateAvatar(UserUpdateVo userUpdateVo, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        UserEntity entity = getById(user.getId());
        if (entity == null) {
            return null;
        }
        if (!StringUtils.isEmpty(userUpdateVo.getUserAvatarUrl())) {
            entity.setUserAvatarUrl(userUpdateVo.getUserAvatarUrl());
            this.updateById(entity);
            return ReturnCodeEnum.SUCCESS;
        }
        return ReturnCodeEnum.DATA_VALID_EXCEPTION;
    }

    @Override
    public ReturnCodeEnum updateInfo(UserUpdateVo userUpdateVo, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        UserEntity entity = getById(user.getId());
        if (entity == null) {
            return ReturnCodeEnum.NO_ALTER_ROLE_POWER;
        }
        if (!StringUtils.isEmpty(userUpdateVo.getUserIntro())) {
            entity.setUserIntro(userUpdateVo.getUserIntro());
        }
        if (!StringUtils.isEmpty(userUpdateVo.getSchool())) {
            entity.setSchool(userUpdateVo.getSchool());
        }
        if (!StringUtils.isEmpty(userUpdateVo.getGrade())) {
            entity.setGrade(userUpdateVo.getGrade());
        }
        if (!StringUtils.isEmpty(userUpdateVo.getMajor())) {
            entity.setMajor(userUpdateVo.getMajor());
        }
        if (!StringUtils.isEmpty(userUpdateVo.getUserQq())) {
            entity.setUserQq(userUpdateVo.getUserQq());
        }
        this.updateById(entity);
        return ReturnCodeEnum.SUCCESS;
    }

    @Override
    @Transactional(rollbackFor = {})
    public ReturnCodeEnum register(RegisterUserVo registerUserVo, HttpServletRequest request) {
        // 验证验证码
        verifyCodeService.verify(request.getSession().getId(), registerUserVo.getVerifyCode());
        //
        UserEntity sysUser = getById(registerUserVo.getUserId());
        if (sysUser != null) {
            throw new UserDataFormatException("你已经注册过了，请直接登录！如果忘记密码，请点击忘记密码！");
        }
        UserEntity byEmail = findByEmail(registerUserVo.getEmail());
        if (byEmail != null) {
            throw new UserDataFormatException("该邮箱已被绑定，请更换邮箱或直接登录！");
        }
        UserEntity byPhoneNumber = findByPhoneNumber(registerUserVo.getPhoneNumber());
        if (byPhoneNumber != null) {
            throw new UserDataFormatException("该手机号已被绑定，请更换手机号或直接登录！");
        }
        UserEntity userEntity = new UserEntity();
        // 注册数据初始化
        userEntity.initData();
        // 如果开启了邀请码验证
        if (baseWebInfoConfig.getRegisterInvitationCode() == 1) {
            inviteCodeService.checkCode(registerUserVo);
        }
        BeanUtils.copyProperties(registerUserVo, userEntity);
        userEntity.setPassword(bCryptPasswordEncoder.encode(registerUserVo.getPassword()));
        this.save(userEntity);
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setRole(RoleTypeEnum.STUDENT.getRole());
        userRoleEntity.setCreateTime(System.currentTimeMillis());
        userRoleEntity.setUserId(userEntity.getUserId());
        userRoleEntity.setOperator("system_create");
        userRoleService.save(userRoleEntity);
        return ReturnCodeEnum.SUCCESS;
    }

    @Override
    public ReturnCodeEnum forgetPassword(ForgetPasswordVo forgetPasswordVo) {
        verifyCodeService.verify(CustomConstant.VERIFY_HASH_KEY + forgetPasswordVo.getEmail(),
                forgetPasswordVo.getCode());
        UserEntity userEntity = this.findByEmail(forgetPasswordVo.getEmail());
        if (userEntity == null) {
            throw new UserDataFormatException("邮箱不存在！");
        }
        if (forgetPasswordVo.getPassword().equals(forgetPasswordVo.getNewPassword())) {
            UserEntity user = new UserEntity();
            user.setUserId(userEntity.getUserId());
            user.setPassword(bCryptPasswordEncoder.encode(forgetPasswordVo.getPassword()));
            this.updateById(user);
            return ReturnCodeEnum.SUCCESS;
        } else {
            throw new UserDataFormatException("两次密码不一致！");
        }
    }

    /**
     * 向课程导入学生
     */
    private void classImportUser(List<AdminAddUser> userList, Map<String, String> map, Map<String,
            StudentsCurriculumEntity> stringStudentsCurriculumEntityMap, Map<String, UserEntity> userEntityMap,
                                 List<StudentsCurriculumEntity> students, AtomicInteger count, Long courseNumber) {
        userList.forEach((u) -> {
            if (stringStudentsCurriculumEntityMap.get(u.getUserId()) != null) {
                map.put(u.getUserId(), "已经在班级中，无需加入!");
            } else {
                UserEntity userEntity = userEntityMap.get(u.getUserId());
                if (userEntity != null) {
                    students.add(initStudentsCurriculumEntity(u, courseNumber, userEntity));
                    count.getAndIncrement();
                    map.put(u.getUserId(), "加入成功!");
                } else {
                    map.put(u.getUserId(), "学生不存在，请让学生注册后再导入！");
                }
            }
        });
    }

    private boolean checkRole(CurriculumEntity curriculumEntity, Claims teacher) {
        if (curriculumEntity.getCreateTeacher().equals(teacher.getId())) {
            return true;
        }
        StudentsCurriculumEntity studentsCurriculumEntity =
                studentsCurriculumService.selectStudentByCurriculumId(teacher.getId(), curriculumEntity.getId());
        if (studentsCurriculumEntity != null) {
            return RoleTypeEnum.TEACHER.getRole().equals(studentsCurriculumEntity.getRole());
        }
        return false;
    }


    public StudentsCurriculumEntity initStudentsCurriculumEntity(AdminAddUser adminAddUser, long courseNumber, UserEntity userEntity) {
        StudentsCurriculumEntity studentsCurriculumEntity = new StudentsCurriculumEntity();
        studentsCurriculumEntity.setCurriculumId(courseNumber);
        studentsCurriculumEntity.setStudentId(adminAddUser.getUserId());
        studentsCurriculumEntity.setCreateTime(System.currentTimeMillis());
        studentsCurriculumEntity.setStudentName(userEntity.getUsername());
        studentsCurriculumEntity.setRole(RoleTypeEnum.STUDENT.getRole());
        return studentsCurriculumEntity;
    }


    private void initUser(UserEntity userEntity, HttpServletRequest request) {
        userEntity.setCreateTime(System.currentTimeMillis());
        userEntity.setLatestLoginTime(System.currentTimeMillis());
        userEntity.setLatestLoginIp(IpUtil.getIpAddr(request));
        userEntity.setUserAvatarUrl("/images/head.png");
        userEntity.setTopImgUrl("/images/top.png");
    }


    /**
     * 判断用户状态
     * 如果是禁言或者封禁状态，则判断禁言时间是否已过，如时间已过
     * 则变回正常状态
     *
     * @param userEntity 用户
     */
    private void checkUserStatus(UserEntity userEntity) {
        if (userEntity.getStatus() != UserStatusEnum.NORMAL.getCode()) {
            if (System.currentTimeMillis() >= userEntity.getLockTime() + userEntity.getStartLockTime()) {
                userEntity.setStatus(UserStatusEnum.NORMAL.getCode());
                userEntity.setStartLockTime(0L);
                userEntity.setLockTime(0L);
                this.updateById(userEntity);
            }
        }
    }

    /**
     * 负责返回班级学生列表已经班级内的角色信息
     *
     * @param userEntityList 要返回的学生列表
     * @param teacher        当前班级班主任
     * @param role           负责填充班级角色内信息
     * @return 班级学生列表
     */
    public List<UserAndRole> addRoleAndCleanPassword(List<UserEntity> userEntityList, String teacher, Map<String, StudentsCurriculumEntity> role) {
        List<UserRoleEntity> roleEntityList = userRoleDao.selectRoleByUserList(userEntityList);
        Map<String, UserRoleEntity> roleMap = new HashMap<>();
        for (UserRoleEntity userRoleEntity : roleEntityList) {
            roleMap.put(userRoleEntity.getUserId(), userRoleEntity);
        }
        boolean flag = false;
        if (role == null) {
            flag = true;
        }
        List<UserAndRole> userAndRoles = new ArrayList<>();
        boolean finalFlag = flag;
        userEntityList.forEach((u) -> {
            if (!u.getUserId().equals(teacher)) {
                UserAndRole userAndRole = new UserAndRole();
                BeanUtils.copyProperties(u, userAndRole);
                if (finalFlag) {
                    userAndRole.setClassRole(u.getRole());
                } else {
                    userAndRole.setClassRole(role.get(u.getUserId()).getRole());
                }

                userAndRole.setRole(roleMap.get(u.getUserId()));
                userAndRole.setCreateTime(TimeUtils.formatTime(u.getCreateTime()));
                userAndRole.setLatestLoginTime(TimeUtils.formatTime(u.getLatestLoginTime()));
                userAndRoles.add(userAndRole);
            }
        });
        return userAndRoles;
    }
}
