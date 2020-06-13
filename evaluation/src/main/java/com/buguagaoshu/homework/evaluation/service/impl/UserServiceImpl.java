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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public final UserRoleService userRoleService;

    public final UserRoleDao userRoleDao;

    private final StudentsCurriculumService studentsCurriculumService;

    private CurriculumService curriculumService;

    @Autowired
    public void setCurriculumService(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserRoleService userRoleService, UserRoleDao userRoleDao, StudentsCurriculumService studentsCurriculumService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRoleService = userRoleService;

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

        // 处理封禁状态
        userEntityList.forEach(this::checkUserStatus);

        if (userEntityList.size() > 0) {
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

        for (UserEntity userEntity: validUser) {
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
                addRoleAndCleanPassword(studentsCurriculumEntities);
                page.setRecords(studentsCurriculumEntities);
                page.setTotal(studentsCurriculumEntities.size());
                return new PageUtils(page);
            }
            return null;

        }
        // 学号查找
        String userId = (String) params.get("nameId");
        QueryWrapper<StudentsCurriculumEntity> studentsCurriculumEntityQueryWrapper =
                new QueryWrapper<>();
        studentsCurriculumEntityQueryWrapper.eq("curriculum_id", courseId);
        if (!StringUtils.isEmpty(userId)) {
            studentsCurriculumEntityQueryWrapper.eq("student_id", userId);
        }
        IPage<StudentsCurriculumEntity> page = studentsCurriculumService.page(
                new Query<StudentsCurriculumEntity>().getPage(params),
                studentsCurriculumEntityQueryWrapper
        );
        List<String> userIds = page.getRecords().stream().map(StudentsCurriculumEntity::getStudentId).collect(Collectors.toList());
        List<UserEntity> userEntities = this.listByIds(userIds);

        // 补全角色
        addRoleAndCleanPassword(userEntities);
        IPage<UserEntity> iPage = new IPage<UserEntity>() {
            @Override
            public List<OrderItem> orders() {
                return null;
            }

            @Override
            public List<UserEntity> getRecords() {
                return userEntities;
            }

            @Override
            public IPage<UserEntity> setRecords(List<UserEntity> records) {
                return null;
            }

            @Override
            public long getTotal() {
                return page.getTotal();
            }

            @Override
            public IPage<UserEntity> setTotal(long total) {
                return null;
            }

            @Override
            public long getSize() {
                return page.getSize();
            }

            @Override
            public IPage<UserEntity> setSize(long size) {
                return null;
            }

            @Override
            public long getCurrent() {
                return page.getCurrent();
            }

            @Override
            public IPage<UserEntity> setCurrent(long current) {
                return null;
            }
        };
        return new PageUtils(iPage);
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
     * @param userEntity 用户
     * */
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

    public void addRoleAndCleanPassword(List<UserEntity> userEntityList) {
        List<UserRoleEntity> roleEntityList = userRoleDao.selectRoleByUserList(userEntityList);
        Map<String, UserRoleEntity> roleMap = new HashMap<>();
        for (UserRoleEntity userRoleEntity : roleEntityList) {
            roleMap.put(userRoleEntity.getUserId(), userRoleEntity);
        }
        // TODO 排除老师本人
        userEntityList.forEach((u)->{
            u.setPassword("");
            u.setRole(roleMap.get(u.getUserId()).getRole());
            u.setRoleEntity(roleMap.get(u.getUserId()));
        });
    }

}
