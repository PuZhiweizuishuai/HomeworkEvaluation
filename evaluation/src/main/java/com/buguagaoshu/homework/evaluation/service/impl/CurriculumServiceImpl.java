package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.buguagaoshu.homework.common.domain.CustomPage;
import com.buguagaoshu.homework.common.enums.*;
import com.buguagaoshu.homework.evaluation.cache.CourseTagCache;
import com.buguagaoshu.homework.evaluation.cache.WebsiteIndexMessageCache;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.CurriculumModel;
import com.buguagaoshu.homework.evaluation.model.JoinCourseCode;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import com.buguagaoshu.homework.evaluation.vo.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.CurriculumDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * @author puzhiwei
 */
@Service("curriculumService")
public class CurriculumServiceImpl extends ServiceImpl<CurriculumDao, CurriculumEntity> implements CurriculumService {

    private final BCryptPasswordEncoder encoder;

    private final StudentsCurriculumService studentsCurriculumService;

    private UserService userService;

    private NotificationService notificationService;

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private final CourseTagCache courseTagCache;

    private final VerifyCodeService verifyCodeService;

    private final WebsiteIndexMessageCache indexMessageCache;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public CurriculumServiceImpl(BCryptPasswordEncoder encoder, StudentsCurriculumService studentsCurriculumService, CourseTagCache courseTagCache, VerifyCodeService verifyCodeService, WebsiteIndexMessageCache indexMessageCache) {
        this.encoder = encoder;
        this.studentsCurriculumService = studentsCurriculumService;

        this.courseTagCache = courseTagCache;
        this.verifyCodeService = verifyCodeService;
        this.indexMessageCache = indexMessageCache;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CurriculumEntity> page = this.page(
                new Query<CurriculumEntity>().getPage(params),
                new QueryWrapper<CurriculumEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = {})
    public CurriculumEntity createCurriculum(CurriculumModel curriculumModel, Claims teacher) {
        CurriculumEntity curriculumEntity = new CurriculumEntity();
        BeanUtils.copyProperties(curriculumModel, curriculumEntity);
        curriculumEntity.setCreateTeacher(teacher.getId());
        curriculumEntity.setTeacherName(teacher.getSubject());
        curriculumEntity.setScore(0.0);
        curriculumEntity.setCreateTime(System.currentTimeMillis());
        curriculumEntity.setUpdateTime(System.currentTimeMillis());
        // 开课结课时间设置
        curriculumEntity.setOpeningTime(TimeUtils.parseTimeNoHour(curriculumModel.getOpeningTime()));
        curriculumEntity.setCloseTime(TimeUtils.parseTimeNoHour(curriculumModel.getCloseTime()));
        if (curriculumEntity.getOpeningTime() > curriculumEntity.getCloseTime()) {
            throw new UserDataFormatException("开课时间不能在结课时间之后");
        }
        // 是否限制加入时间
        if (CurriculumJoinTimLimitEnum.LIMIT_JOIN_TIME.getCode() == curriculumModel.getJoinTimeLimit()) {
            curriculumEntity.setJoinTime(TimeUtils.parseTime(curriculumModel.getJoinTime()));
            if (curriculumEntity.getJoinTime() < curriculumEntity.getOpeningTime() || curriculumEntity.getJoinTime() > curriculumEntity.getCloseTime()) {
                throw new UserDataFormatException("限制课程进入时间必须在开课和结课的区间，不能超过此区间！");
            }
        }
        // 课程进入方式
        if (CurriculumAccessTypeEnum.USE_PASSWORD.getCode() == curriculumEntity.getAccessMethod()) {
            curriculumEntity.setPassword(encoder.encode(curriculumModel.getPassword()));
        }
        // 保存
        curriculumEntity.setId(IdWorker.getId());
        this.save(curriculumEntity);

        // 将课程与老师在学生-课程表进行关联，方便后期查找
        StudentsCurriculumEntity studentsCurriculumEntity
                = new StudentsCurriculumEntity();
        studentsCurriculumEntity.setCreateTime(System.currentTimeMillis());
        studentsCurriculumEntity.setStudentId(teacher.getId());
        studentsCurriculumEntity.setCurriculumId(curriculumEntity.getId());
        studentsCurriculumEntity.setRole(RoleTypeEnum.TEACHER.getRole());
        studentsCurriculumEntity.setStudentName(teacher.getSubject());
        studentsCurriculumService.save(studentsCurriculumEntity);

        // 脱敏
        curriculumEntity.setPassword("");
        return curriculumEntity;
    }

    @Override
    public CurriculumEntity updateCurriculum(CurriculumModel curriculumModel, HttpServletRequest request) {
        // 验证码校验
        verifyCodeService.verify(WebConstant.VERIFY_CODE_KEY, curriculumModel.getVerifyCode(), request.getSession());
        Claims teacher = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        CurriculumEntity curriculumEntity = this.getById(curriculumModel.getId());
        if (curriculumEntity == null) {
            return null;
        }
        if (!curriculumEntity.getCreateTeacher().equals(teacher.getId())) {
            return null;
        }
        BeanUtils.copyProperties(curriculumModel, curriculumEntity);
        // 避免课程创建教师信息被修改
        curriculumEntity.setCreateTeacher(teacher.getId());
        curriculumEntity.setTeacherName(teacher.getSubject());
        curriculumEntity.setUpdateTime(System.currentTimeMillis());

        // 特殊处理开始结束时间
        curriculumEntity.setOpeningTime(timeHandle(curriculumModel.getOpeningTime(),
                curriculumEntity.getOpeningTime(), false));
        curriculumEntity.setCloseTime(timeHandle(curriculumModel.getCloseTime(),
                curriculumEntity.getCloseTime(), false));
        if (curriculumEntity.getOpeningTime() > curriculumEntity.getCloseTime()) {
            throw new UserDataFormatException("开课时间不能在结课时间之后");
        }
        // 是否限制加入时间
        if (CurriculumJoinTimLimitEnum.LIMIT_JOIN_TIME.getCode() == curriculumModel.getJoinTimeLimit()) {
            Long limitTime = timeHandle(curriculumModel.getJoinTime(), curriculumEntity.getJoinTime(), true);
            if (limitTime == null) {
                throw new UserDataFormatException("提交的限制进入时间有误！");
            }
            curriculumEntity.setJoinTime(limitTime);
            if (curriculumEntity.getJoinTime() < curriculumEntity.getOpeningTime() || curriculumEntity.getJoinTime() > curriculumEntity.getCloseTime()) {
                throw new UserDataFormatException("限制课程进入时间必须在开课和结课的区间，不能超过此区间！");
            }
        }
        // 课程进入方式
        if (CurriculumAccessTypeEnum.USE_PASSWORD.getCode() == curriculumEntity.getAccessMethod()) {
            // TODO 修改密码需要验证
            if (StringUtils.isEmpty(curriculumModel.getPassword())) {
                throw new UserDataFormatException("当前设置课程需要密码才能进入，但是却没有输入密码！");
            }
            curriculumEntity.setPassword(encoder.encode(curriculumModel.getPassword()));
        }
        this.updateById(curriculumEntity);
        return curriculumEntity;
    }

    @Override
    public PageUtils selectUserCurriculumList(Map<String, Object> params, HttpServletRequest request) {
        QueryWrapper<CurriculumEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("update_time");
        String teacher = (String) params.get("teacher");
        if (!StringUtils.isEmpty(teacher)) {
            wrapper.eq("create_teacher", teacher);
        }
        Long tag = null;
        try {
            tag = Long.parseLong((String) params.get("tag"));
        } catch (NumberFormatException ignored) {

        }
        if (tag != null) {
            wrapper.eq("father_course_tag", tag).or().eq("course_tag", tag);
        }

        String userId = (String) params.get("user");
        if (!StringUtils.isEmpty(userId)) {
            return selectJoinCurriculumList(userId, request);
        }


        IPage<CurriculumEntity> page = this.page(
                new Query<CurriculumEntity>().getPage(params),
                wrapper
        );

        page.getRecords().forEach((curriculumEntity -> {
            curriculumEntity.setPassword("");
            // 除去详细介绍，降低网络占用
            curriculumEntity.setCurriculumInfo("");
        }));

        return new PageUtils(page);
    }

    @Override
    public PageUtils selectJoinCurriculumList(String userId, HttpServletRequest request) {
        IPage<StudentsCurriculumEntity> page =
                studentsCurriculumService.page(
                        new Query<StudentsCurriculumEntity>().getPage(new HashMap<>(2)),
                        new QueryWrapper<StudentsCurriculumEntity>().eq("student_id", userId).orderByDesc("create_time")
                );
        List<Long> ids = page.getRecords().stream().map(StudentsCurriculumEntity::getCurriculumId).collect(Collectors.toList());
        if (ids.size() != 0) {
            List<CurriculumEntity> curriculumEntityList = this.listByIds(ids);
            curriculumEntityList.forEach((c) -> {
                c.setPassword("");
                c.setCurriculumInfo("");
            });
            curriculumEntityList.sort(new Comparator<CurriculumEntity>() {
                @Override
                public int compare(CurriculumEntity o1, CurriculumEntity o2) {
                    return o2.getUpdateTime().compareTo(o1.getUpdateTime());
                }
            });
            IPage<CurriculumEntity> entityIPage = new CustomPage<>(curriculumEntityList, page.getTotal(), page.getSize(), page.getCurrent(), page.orders());
            return new PageUtils(entityIPage);
        }
        return null;
    }


    @Override
    public CurriculumInfo info(Long id) {
        CurriculumEntity entity = this.getById(id);
        if (entity == null) {
            return null;
        }
        CurriculumInfo info = new CurriculumInfo();
        BeanUtils.copyProperties(entity, info);
        // 填充老师信息
        UserEntity teacher = userService.getById(info.getCreateTeacher());
        info.setTeacherAvatar(teacher.getUserAvatarUrl());
        info.setTeacherSchool(teacher.getSchool());
        info.setTitle(teacher.getTitle());

        // 填充分类信息
        info.setCourseTagName(
                courseTagCache.
                        getCourseTagEntityMap().
                        get(info.getCourseTag()).
                        getCourseMajor());

        // 获取其它教师列表
        List<SimpleTeacherInfo> teacherInfos = new ArrayList<>();
        List<StudentsCurriculumEntity> userEntityList =
                studentsCurriculumService.teacherList(info.getId(), info.getCreateTeacher());
        if (userEntityList != null && userEntityList.size() != 0) {
            List<String> ids = userEntityList
                    .stream()
                    .map(StudentsCurriculumEntity::getStudentId)
                    .collect(Collectors.toList());
            List<UserEntity> teachers = userService.listByIds(ids);
            teachers.forEach((t) -> {
                SimpleTeacherInfo teacherInfo = new SimpleTeacherInfo();
                teacherInfo.setId(t.getUserId());
                teacherInfo.setName(t.getUsername());
                teacherInfo.setTitle(t.getTitle());
                teacherInfo.setUserAvatar(t.getUserAvatarUrl());
                teacherInfos.add(teacherInfo);
            });
        }
        info.setOtherTeacher(teacherInfos);
        return info;
    }

    @Override
    public ReturnCodeEnum join(Long id, HttpServletRequest request, JoinCourseCode code) {
        // 验证码校验
        verifyCodeService.verify(WebConstant.VERIFY_CODE_KEY, code.getVerifyCode(), request.getSession());
        // 获取当前用户
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        long time = System.currentTimeMillis();
        CurriculumEntity entity = this.getById(id);
        if (entity == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        if (entity.getOpeningTime() > time) {
            return ReturnCodeEnum.NO_TIME;
        }
        if (entity.getCloseTime() < time) {
            return ReturnCodeEnum.COURSE_IS_CLOSE;
        }
        // 限制加入时间
        if (entity.getJoinTimeLimit() == CurriculumJoinTimLimitEnum.LIMIT_JOIN_TIME.getCode()) {
            if (entity.getJoinTime() < time) {
                return ReturnCodeEnum.EXCEED_JOIN_LIMIT_TIME;
            }
        }


        StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), entity.getId());
        if (studentsCurriculumEntity != null) {
            return ReturnCodeEnum.ALREADY_JOINED;
        }
        StudentsCurriculumEntity student = new StudentsCurriculumEntity();
        student.setStudentName(user.getSubject());
        student.setStudentId(user.getId());
        student.setRole(RoleTypeEnum.STUDENT.getRole());
        student.setCurriculumId(entity.getId());
        student.setCreateTime(System.currentTimeMillis());
        // TODO 邀请码加入
        if (entity.getAccessMethod().equals(CurriculumAccessTypeEnum.USE_PASSWORD.getCode())) {
            if (encoder.matches(code.getCode(), entity.getPassword())) {

                this.baseMapper.addCount("student_number", entity.getId(), 1);
                studentsCurriculumService.save(student);
                // 向老师发送通知
                notificationService.send(user.getId(),
                        user.getSubject(),
                        entity.getCreateTeacher(),
                        NotificationTypeEnum.COURSE_JOIN,
                        "学生: " + user.getId() + " " + user.getSubject() + "进入了课程《" + entity.getCurriculumName() + "》",
                        "/user/" + user.getId(),
                        entity.getId());
                return ReturnCodeEnum.SUCCESS;
            }
        } else if (entity.getAccessMethod().equals(CurriculumAccessTypeEnum.PUBLIC_CURRICULUM.getCode())) {
            studentsCurriculumService.save(student);
            this.baseMapper.addCount("student_number", entity.getId(), 1);
            // 向老师发送通知
            notificationService.send(user.getId(),
                    user.getSubject(),
                    entity.getCreateTeacher(),
                    NotificationTypeEnum.COURSE_JOIN,
                    "学生: " + user.getId() + " " + user.getSubject() + "进入了课程《" + entity.getCurriculumName() + "》",
                    "/user/" + user.getId(),
                    entity.getId());
            return ReturnCodeEnum.SUCCESS;
        }
        return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
    }

    @Override
    public Map<String, Object> learn(Long id, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        StudentsCurriculumEntity student = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), id);
        if (student == null) {
            return null;
        }
        CurriculumEntity curriculumEntity = this.getById(id);
        Map<String, Object> map = new HashMap<>(2);
        map.put("course", curriculumEntity);
        map.put("user", student);
        return map;
    }

    @Override
    public void addCount(String col, Long id, Integer count) {
        this.baseMapper.addCount(col, id, count);
    }

    @Override
    public void addCount(String col, Long id, Double count) {
        this.baseMapper.addCount(col, id, count);
    }


    /**
     * 更新时间时对时间的特殊处理
     *
     * @param time       接收到的时间
     * @param systemTime 系统中保存的时间
     * @param isJoinTime 是否是限制加入时间
     */
    private Long timeHandle(String time, Long systemTime, boolean isJoinTime) {
        Long openTime = null;
        try {
            openTime = Long.parseLong(time);
            return openTime;
        } catch (NumberFormatException e) {
            try {
                if (isJoinTime) {
                    openTime = TimeUtils.parseTime(time);
                } else {
                    openTime = TimeUtils.parseTimeNoHour(time);
                }

                return openTime;
            } catch (Exception ignored) {
            }
        }
        return systemTime;
    }
}
