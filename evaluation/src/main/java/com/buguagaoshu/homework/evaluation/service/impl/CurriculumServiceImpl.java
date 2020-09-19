package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.CustomPage;
import com.buguagaoshu.homework.common.enums.CurriculumAccessTypeEnum;
import com.buguagaoshu.homework.common.enums.CurriculumJoinTimLimitEnum;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.cache.CourseTagCache;
import com.buguagaoshu.homework.evaluation.cache.WebsiteIndexMessageCache;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.CurriculumModel;
import com.buguagaoshu.homework.evaluation.model.JoinCourseCode;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.service.VerifyCodeService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import com.buguagaoshu.homework.evaluation.vo.*;
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

import com.buguagaoshu.homework.evaluation.dao.CurriculumDao;
import com.buguagaoshu.homework.evaluation.service.CurriculumService;
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
    public CurriculumEntity createCurriculum(CurriculumModel curriculumModel, Claims teacher) {
        CurriculumEntity curriculumEntity = new CurriculumEntity();
        BeanUtils.copyProperties(curriculumModel, curriculumEntity);
        curriculumEntity.setCreateTeacher(teacher.getId());
        curriculumEntity.setTeacherName(teacher.getSubject());
        curriculumEntity.setScore(0.0);
        curriculumEntity.setCreateTime(System.currentTimeMillis());
        curriculumEntity.setUpdateTime(System.currentTimeMillis());
        // 开课结课时间设置
        curriculumEntity.setOpeningTime(TimeUtils.parseTimeZone(curriculumModel.getOpeningTime()));
        curriculumEntity.setCloseTime(TimeUtils.parseTimeZone(curriculumModel.getCloseTime()));
        // 是否限制加入时间
        if (CurriculumJoinTimLimitEnum.LIMIT_JOIN_TIME.getCode() == curriculumModel.getJoinTimeLimit()) {
            curriculumEntity.setJoinTime(TimeUtils.parseTimeZone(curriculumModel.getJoinTime()));
        }
        // 课程进入方式
        if (CurriculumAccessTypeEnum.USE_PASSWORD.getCode() == curriculumEntity.getAccessMethod()) {
            curriculumEntity.setPassword(encoder.encode(curriculumModel.getPassword()));
        }
        // 保存
        this.save(curriculumEntity);

        // 将课程与老师在学生-课程表进行关联，方便后期查找
        StudentsCurriculumEntity studentsCurriculumEntity
                = new StudentsCurriculumEntity();
        studentsCurriculumEntity.setCreateTime(System.currentTimeMillis());
        studentsCurriculumEntity.setStudentId(teacher.getId());
        studentsCurriculumEntity.setCurriculumId(curriculumEntity.getId());
        studentsCurriculumEntity.setRole(RoleTypeEnum.TEACHER.getRole());
        studentsCurriculumService.save(studentsCurriculumEntity);

        // 脱敏
        curriculumEntity.setPassword("");
        return curriculumEntity;
    }

    @Override
    public CurriculumEntity updateCurriculum(CurriculumModel curriculumModel, Claims teacher) {
        CurriculumEntity curriculumEntity = this.getById(curriculumModel.getId());
        if (curriculumEntity == null) {
            return null;
        }
        if (!curriculumEntity.getCreateTeacher().equals(teacher.getId())) {
            return null;
        }
        BeanUtils.copyProperties(curriculumModel, curriculumEntity);

        curriculumEntity.setUpdateTime(System.currentTimeMillis());

        // 特殊处理开始结束时间
        curriculumEntity.setOpeningTime(timeHandle(curriculumModel.getOpeningTime(),
                curriculumEntity.getOpeningTime()));
        curriculumEntity.setCloseTime(timeHandle(curriculumModel.getCloseTime(),
                curriculumEntity.getCloseTime()));

        // 是否限制加入时间
        if (CurriculumJoinTimLimitEnum.LIMIT_JOIN_TIME.getCode() == curriculumModel.getJoinTimeLimit()) {
            Long limitTime = timeHandle(curriculumModel.getJoinTime(), curriculumEntity.getJoinTime());
            if (limitTime == null) {
                throw new UserDataFormatException("提交的限制进入时间有误！");
            }
            curriculumEntity.setJoinTime(limitTime);
        }
        // 课程进入方式
        if (CurriculumAccessTypeEnum.USE_PASSWORD.getCode() == curriculumEntity.getAccessMethod()) {
            // TODO 修改密码需要验证
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
        if (teacher != null) {
            wrapper.eq("create_teacher", teacher);
        }
        Long tag = null;
        try {
            tag = Long.parseLong((String) params.get("tag"));
        } catch (NumberFormatException ignored) {

        }
        if (tag != null) {
            wrapper.eq("father_course_tag",tag).or().eq("course_tag", tag);
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
        if (!userId.equals(JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId())) {
            return null;
        }
        IPage<StudentsCurriculumEntity> page =
                studentsCurriculumService.page(
                new Query<StudentsCurriculumEntity>().getPage(new HashMap<>(2)),
                new QueryWrapper<StudentsCurriculumEntity>().eq("student_id", userId)
        );
        List<Long> ids = page.getRecords().stream().map(StudentsCurriculumEntity::getCurriculumId).collect(Collectors.toList());
        if (ids.size() != 0) {
            List<CurriculumEntity> curriculumEntityList = this.listByIds(ids);
            curriculumEntityList.forEach((c)->{
                c.setPassword("");
                c.setCurriculumInfo("");
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
        HttpSession session = request.getSession(false);
        String verifyCodeKey = (String) session.getAttribute("verifyCodeKey");
        // 验证码校验
        verifyCodeService.verify(verifyCodeKey, code.getVerifyCode());
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
        // TODO 向老师发送通知
        if (entity.getAccessMethod().equals(CurriculumAccessTypeEnum.USE_PASSWORD.getCode())) {
            if (encoder.matches(code.getCode(), entity.getPassword())) {
                // TODO 优化加 1 方式
                entity.setStudentNumber(entity.getStudentNumber() + 1);
                this.updateById(entity);
                studentsCurriculumService.save(student);
                return ReturnCodeEnum.SUCCESS;
            }
        } else if (entity.getAccessMethod().equals(CurriculumAccessTypeEnum.PUBLIC_CURRICULUM.getCode())) {
            studentsCurriculumService.save(student);
            // TODO 优化加 1 方式
            entity.setStudentNumber(entity.getStudentNumber() + 1);
            this.updateById(entity);
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


    private Long timeHandle(String time, Long systemTime) {
        Long openTime = null;
        try {
            openTime = Long.parseLong(time);
            return openTime;
        } catch (NumberFormatException e) {
            try {
                openTime = TimeUtils.parseTimeZone(time);
                return openTime;
            } catch (Exception ignored) {}
        }
        return systemTime;
    }
}
