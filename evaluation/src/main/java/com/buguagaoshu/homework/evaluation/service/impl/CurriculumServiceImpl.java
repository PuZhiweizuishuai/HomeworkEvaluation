package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.buguagaoshu.homework.common.enums.CurriculumAccessTypeEnum;
import com.buguagaoshu.homework.common.enums.CurriculumJoinTimLimitEnum;
import com.buguagaoshu.homework.evaluation.cache.CourseTagCache;
import com.buguagaoshu.homework.evaluation.cache.WebsiteIndexMessageCache;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.CurriculumModel;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.service.UserService;
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


/**
 * @author puzhiwei
 */
@Service("curriculumService")
public class CurriculumServiceImpl extends ServiceImpl<CurriculumDao, CurriculumEntity> implements CurriculumService {

    private final BCryptPasswordEncoder encoder;

    private final StudentsCurriculumService studentsCurriculumService;

    private final UserService userService;

    private final CourseTagCache courseTagCache;

    private final WebsiteIndexMessageCache indexMessageCache;

    @Autowired
    public CurriculumServiceImpl(BCryptPasswordEncoder encoder, StudentsCurriculumService studentsCurriculumService, UserService userService, CourseTagCache courseTagCache, WebsiteIndexMessageCache indexMessageCache) {
        this.encoder = encoder;
        this.studentsCurriculumService = studentsCurriculumService;
        this.userService = userService;
        this.courseTagCache = courseTagCache;
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
            IPage<CurriculumEntity> entityIPage = new IPage<CurriculumEntity>() {
                @Override
                public List<OrderItem> orders() {
                    return page.orders();
                }

                @Override
                public List<CurriculumEntity> getRecords() {
                    return curriculumEntityList;
                }

                @Override
                public IPage<CurriculumEntity> setRecords(List<CurriculumEntity> records) {
                    return null;
                }

                @Override
                public long getTotal() {
                    return page.getTotal();
                }

                @Override
                public IPage<CurriculumEntity> setTotal(long total) {
                    return null;
                }

                @Override
                public long getSize() {
                    return page.getSize();
                }

                @Override
                public IPage<CurriculumEntity> setSize(long size) {
                    return null;
                }

                @Override
                public long getCurrent() {
                    return page.getCurrent();
                }

                @Override
                public IPage<CurriculumEntity> setCurrent(long current) {
                    return null;
                }
            };
            return new PageUtils(entityIPage);
        }
        return null;
    }

    @Override
    public CurriculumEntity judgeThisCurriculumUserSelect(Long courseId, String userId) {
        StudentsCurriculumEntity studentsCurriculumEntity =
                studentsCurriculumService.selectStudentByCurriculumId(userId, courseId);
        if (studentsCurriculumEntity == null) {
            return null;
        }
        return this.getById(courseId);
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
                studentsCurriculumService.teacherList(info.getId());
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
