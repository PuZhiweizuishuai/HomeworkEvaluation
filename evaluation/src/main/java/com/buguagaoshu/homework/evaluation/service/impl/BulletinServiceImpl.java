package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.NotificationTypeEnum;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.NotificationService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.BulletinDao;
import com.buguagaoshu.homework.evaluation.entity.BulletinEntity;
import com.buguagaoshu.homework.evaluation.service.BulletinService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


@Service("bulletinService")
public class BulletinServiceImpl extends ServiceImpl<BulletinDao, BulletinEntity> implements BulletinService {

    private final StudentsCurriculumService studentsCurriculumService;

    private final NotificationService notificationService;

    @Autowired
    public BulletinServiceImpl(StudentsCurriculumService studentsCurriculumService, NotificationService notificationService) {
        this.studentsCurriculumService = studentsCurriculumService;
        this.notificationService = notificationService;
    }

    @Override
    public PageUtils queryPage(Long id, Map<String, Object> params, HttpServletRequest request) {
        // 获取当前用户，鉴权
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        StudentsCurriculumEntity student = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), id);
        if (student == null) {
            return null;
        }
        QueryWrapper<BulletinEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("curriculum_id", id);
        if (!student.getRole().equals(RoleTypeEnum.TEACHER.getRole())) {
            wrapper.eq("status", 0);
        }
        wrapper.orderByDesc("update_time");
        IPage<BulletinEntity> page = this.page(
                new Query<BulletinEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }


    @Override
    @Transactional(rollbackFor = {})
    public ReturnCodeEnum saveBulletin(BulletinEntity bulletinEntity, HttpServletRequest request) {
        // 获取当前用户
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        // 检查有无权限
        List<StudentsCurriculumEntity> teacherList =
                studentsCurriculumService.teacherList(bulletinEntity.getCurriculumId());
        if (teacherList != null && teacherList.size() != 0) {
            for (StudentsCurriculumEntity teacher : teacherList) {
                // 如果是这门课的老师
                if (user.getId().equals(teacher.getStudentId())) {
                    save(bulletinEntity, user, IpUtil.getIpAddr(request), IpUtil.getUa(request));
                    // 向学生发布消息通知
                    List<StudentsCurriculumEntity> list = studentsCurriculumService.findUserListInCurriculum(bulletinEntity.getCurriculumId());
                    notificationService.sendBulletin(list, bulletinEntity.getUserId(),user.getSubject(), bulletinEntity.getCurriculumId());
                    return ReturnCodeEnum.SUCCESS;
                }
            }
        }
        return ReturnCodeEnum.NO_POWER;
    }

    @Override
    public ReturnCodeEnum updateBulletin(BulletinEntity bulletinEntity, HttpServletRequest request) {
        if (bulletinEntity.getId() == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        BulletinEntity sys = this.getById(bulletinEntity.getId());
        if (sys == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        if (bulletinEntity.getUserId().equals(user.getId())) {
            sys.setUpdateTime(System.currentTimeMillis());
            sys.setStatus(bulletinEntity.getStatus());
            sys.setText(bulletinEntity.getText());
            sys.setTitle(bulletinEntity.getTitle());
            sys.setIp(IpUtil.getIpAddr(request));
            sys.setUa(IpUtil.getUa(request));
            this.updateById(sys);
            return ReturnCodeEnum.SUCCESS;
        } else {
            return ReturnCodeEnum.NO_POWER;
        }

    }


    public void save(BulletinEntity bulletinEntity, Claims user, String ip, String ua) {
        long time = System.currentTimeMillis();
        bulletinEntity.setUserId(user.getId());
        bulletinEntity.setCreateTime(time);
        bulletinEntity.setUpdateTime(time);
        bulletinEntity.setUa(ua);
        bulletinEntity.setIp(ip);
        bulletinEntity.setStatus(0);
        this.save(bulletinEntity);
    }

}