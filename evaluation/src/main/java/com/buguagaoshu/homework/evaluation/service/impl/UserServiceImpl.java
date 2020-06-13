package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.buguagaoshu.homework.common.enums.PasswordStatusEnum;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.common.enums.UserStatusEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.dao.UserRoleDao;
import com.buguagaoshu.homework.evaluation.entity.CurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.CurriculumService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.AdminAddUser;
import com.buguagaoshu.homework.evaluation.vo.AlterUserStatus;
import com.buguagaoshu.homework.evaluation.vo.UserAndRole;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;
import com.buguagaoshu.homework.evaluation.utils.InviteCodeUtil;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.UserDao;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


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

    @Autowired
    public void setCurriculumService(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @Autowired
    public void setUserRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserRoleDao userRoleDao, StudentsCurriculumService studentsCurriculumService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;


        this.userRoleDao = userRoleDao;
        this.studentsCurriculumService = studentsCurriculumService;
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
//        String role = (String) params.get("role");
//        if (role != null) {
//            wrapper.eq("")
//        }
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                wrapper
        );

        PageUtils pageUtils = new PageUtils(page);


        List<UserEntity> userEntityList = (List<UserEntity>) pageUtils.getList();


        if (userEntityList.size() > 0) {
            // 处理封禁状态
            userEntityList.forEach(this::checkUserStatus);
            List<UserRoleEntity> roleEntityList =
                    userRoleDao.selectRoleByUserList(userEntityList);


            Map<String, UserRoleEntity> roleMap = new HashMap<>(pageUtils.getTotalCount());
            for (UserRoleEntity userRoleEntity : roleEntityList) {
                roleMap.put(userRoleEntity.getUserId(), userRoleEntity);
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
        }
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
        if (!curriculumEntity.getCreateTeacher().equals(nowLoginUser.getId())) {
            return null;
        }

        String name = (String) params.get("name");
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        // 姓名查找
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("username", name);
            IPage<UserEntity> page = this.page(
                    new Query<UserEntity>().getPage(params),
                    wrapper
            );
            List<UserEntity> userEntityList = page.getRecords();
            if (userEntityList != null && userEntityList.size() != 0) {
                List<UserEntity> studentsCurriculumEntities =
                        studentsCurriculumService.findUserByIdAndCurriculumId(userEntityList,
                                curriculumEntity.getId());
                // 补全角色
                if (studentsCurriculumEntities != null && studentsCurriculumEntities.size() > 0) {
                    List<UserAndRole> userAndRoles = addRoleAndCleanPassword(studentsCurriculumEntities, nowLoginUser.getId(), null);

                    return new PageUtils(userAndRoleIPage(userAndRoles, page));
                }
                return null;
            }
            return null;

        }
        // 学号查找
        String userId = (String) params.get("nameId");
        QueryWrapper<StudentsCurriculumEntity> studentsCurriculumEntityQueryWrapper =
                new QueryWrapper<>();
        studentsCurriculumEntityQueryWrapper.eq("curriculum_id", courseId);
        if (!StringUtils.isEmpty(userId)) {
            studentsCurriculumEntityQueryWrapper.like("student_id", userId);
        }
        IPage<StudentsCurriculumEntity> page = studentsCurriculumService.page(
                new Query<StudentsCurriculumEntity>().getPage(params),
                studentsCurriculumEntityQueryWrapper
        );
        // 补全班级内角色信息
//        List<String> userIds =
//                page.getRecords()
//                        .stream()
//                        .map(StudentsCurriculumEntity::getStudentId)
//                        .collect(Collectors.toList());
        Map<String, StudentsCurriculumEntity> rolesMap =
                page.getRecords()
                .stream()
                .collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, u -> u));

        List<UserEntity> userEntities = this.listByIds(rolesMap.keySet());
        if (userEntities != null && userEntities.size() > 0) {
            // 补全角色
            List<UserAndRole> userAndRoles = addRoleAndCleanPassword(userEntities, nowLoginUser.getId(), rolesMap);

            return new PageUtils(userAndRoleIPage(userAndRoles, page));
        }
        return null;
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
            // 判断当前用户是否有权限进行此操作
            if (checkRole(curriculumEntity, teacher)) {
                // 查找当前课程学生列表，避免重复加入
                List<UserEntity> userEntities = this.listByIds(userList.stream().map(AdminAddUser::getUserId).collect(Collectors.toList()));
                if (userEntities == null) {
                    return null;
                }
                Map<String, UserEntity> userEntityMap = userEntities.stream().collect(Collectors.toMap(UserEntity::getUserId, u -> u));
                List<StudentsCurriculumEntity> studentsCurriculumEntities =
                        studentsCurriculumService.list(new QueryWrapper<StudentsCurriculumEntity>().eq("curriculum_id", courseNumber));
                // 直接导入
                if (studentsCurriculumEntities == null && studentsCurriculumEntities.size() == 0) {
                    // 记录学生数
                    AtomicInteger count = new AtomicInteger(0);
                    List<StudentsCurriculumEntity> students = new ArrayList<>();
                    userList.forEach((u) -> {
                        if (userEntityMap.get(u.getUserId()) != null) {
                            count.getAndIncrement();
                            map.put(u.getUserId(), "加入成功!");
                            students.add(initStudentsCurriculumEntity(u, courseNumber));
                        } else {
                            map.put(u.getUserId(), "学生不存在");
                        }
                    });
                    studentsCurriculumService.saveBatch(students);
                    // 保存课程人数
                    curriculumEntity.setStudentNumber(count.get());
                    // 导入部分
                } else {
                    assert studentsCurriculumEntities != null;
                    Map<String, StudentsCurriculumEntity> stringStudentsCurriculumEntityMap
                            = studentsCurriculumEntities.stream().collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, u -> u));
                    List<StudentsCurriculumEntity> students = new ArrayList<>();
                    AtomicInteger count = new AtomicInteger(0);
                    userList.forEach((u) -> {
                        if (stringStudentsCurriculumEntityMap.get(u.getUserId()) != null) {
                            map.put(u.getUserId(), "已经在班级中，无需加入!");
                        } else {
                            if (userEntityMap.get(u.getUserId()) != null) {
                                students.add(initStudentsCurriculumEntity(u, courseNumber));
                                count.getAndIncrement();
                                map.put(u.getUserId(), "加入成功!");
                            } else {
                                map.put(u.getUserId(), "学生不存在");
                            }
                        }
                    });
                    studentsCurriculumService.saveBatch(students);
                    // 保存课程人数
                    curriculumEntity.setStudentNumber(count.get() + curriculumEntity.getStudentNumber());
                }
            } else {
                return null;
            }
            CurriculumEntity entity = new CurriculumEntity();
            entity.setId(curriculumEntity.getId());
            entity.setStudentNumber(curriculumEntity.getStudentNumber());
            curriculumService.updateById(entity);
            return map;
        }
        return null;
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


    public StudentsCurriculumEntity initStudentsCurriculumEntity(AdminAddUser adminAddUser, long courseNumber) {
        StudentsCurriculumEntity studentsCurriculumEntity = new StudentsCurriculumEntity();
        studentsCurriculumEntity.setCurriculumId(courseNumber);
        studentsCurriculumEntity.setStudentId(adminAddUser.getUserId());
        studentsCurriculumEntity.setCreateTime(System.currentTimeMillis());
        studentsCurriculumEntity.setRole(RoleTypeEnum.USER.getRole());
        return studentsCurriculumEntity;
    }

    public void addStudentCurriculumRelationship(String id,
                                                 String role,
                                                 String curriculumId,
                                                 String teacher) {

    }


    private void initUser(UserEntity userEntity, HttpServletRequest request) {
        userEntity.setCreateTime(System.currentTimeMillis());
        userEntity.setLatestLoginTime(System.currentTimeMillis());
        userEntity.setLatestLoginIp(IpUtil.getIpAddr(request));
        userEntity.setUserAvatarUrl("https://ae01.alicdn.com/kf/H2c8876c2525f47fe9266aba1bf0de9b6e.png");
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
     * @param userEntityList 要返回的学生列表
     * @param teacher 当前班级班主任
     * @param role 负责填充班级角色内信息
     * @return 班级学生列表
     * */
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


    public IPage<UserAndRole> userAndRoleIPage(List<UserAndRole> userAndRoles, IPage<?> page) {
        IPage<UserAndRole> userAndRoleIPage = new IPage<UserAndRole>() {
            @Override
            public List<OrderItem> orders() {
                return null;
            }

            @Override
            public List<UserAndRole> getRecords() {
                return userAndRoles;
            }

            @Override
            public IPage<UserAndRole> setRecords(List<UserAndRole> records) {
                return null;
            }

            @Override
            public long getTotal() {
                return page.getTotal() - 1;
            }

            @Override
            public IPage<UserAndRole> setTotal(long total) {
                return null;
            }

            @Override
            public long getSize() {
                return page.getSize();
            }

            @Override
            public IPage<UserAndRole> setSize(long size) {
                return null;
            }

            @Override
            public long getCurrent() {
                return page.getCurrent();
            }

            @Override
            public IPage<UserAndRole> setCurrent(long current) {
                return null;
            }
        };
        return userAndRoleIPage;
    }
}
