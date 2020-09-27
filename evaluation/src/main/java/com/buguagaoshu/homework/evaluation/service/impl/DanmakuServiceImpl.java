package com.buguagaoshu.homework.evaluation.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.dao.DanmakuDao;
import com.buguagaoshu.homework.evaluation.entity.CoursewareEntity;
import com.buguagaoshu.homework.evaluation.entity.DanmakuEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.CoursewareService;
import com.buguagaoshu.homework.evaluation.service.DanmakuService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.utils.DanmakuUtils;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.DanmakuVo;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.*;



import javax.servlet.http.HttpServletRequest;

/**
 * @author Pu Zhiwei
 * */
@Service("danmakuService")
public class DanmakuServiceImpl extends ServiceImpl<DanmakuDao, DanmakuEntity> implements DanmakuService {

    private final CoursewareService coursewareService;

    private final StudentsCurriculumService studentsCurriculumService;

    public DanmakuServiceImpl(CoursewareService coursewareService, StudentsCurriculumService studentsCurriculumService) {
        this.coursewareService = coursewareService;
        this.studentsCurriculumService = studentsCurriculumService;
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<DanmakuEntity> page = this.page(
                new Query<DanmakuEntity>().getPage(params),
                new QueryWrapper<DanmakuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<Object> danmakuList(Long id, Integer max) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("limit", max);
        IPage<DanmakuEntity> page = this.page(
                new Query<DanmakuEntity>().getPage(params),
                new QueryWrapper<DanmakuEntity>().eq("courseware_id", id)
        );
        List<DanmakuEntity> danmakuEntities = page.getRecords();

       List<Object> danmakuDtos = new LinkedList<>();
       danmakuEntities.forEach(d->{
           danmakuDtos.add(DanmakuUtils.createDanmaku(
                   d.getTime(),
                   d.getType(),
                   d.getColorDec(),
                   d.getColor(),
                   d.getText()
           ));

       });
       return danmakuDtos;
    }

    @Override
    public ReturnCodeEnum saveDanmaku(DanmakuVo danmakuVo, HttpServletRequest request) {
        Claims claims = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        if (claims == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }

        CoursewareEntity byId = coursewareService.getById(danmakuVo.getId());
        if (byId == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(claims.getId(), byId.getCourseId());
        if (studentsCurriculumEntity == null) {
            return ReturnCodeEnum.NO_ROLE_OR_NO_FOUND;
        }
        DanmakuEntity danmakuEntity = new DanmakuEntity();
        danmakuEntity.setAuthor(claims.getId());

        danmakuEntity.setColorDec(danmakuVo.getColor());
        danmakuEntity.setCoursewareId(danmakuVo.getId());
        danmakuEntity.setText(danmakuVo.getText());
        danmakuEntity.setColor(Long.toHexString(danmakuVo.getColor()));
        danmakuEntity.setTime(danmakuVo.getTime());
        danmakuEntity.setType(danmakuVo.getType());
        this.save(danmakuEntity);
        // TODO 加入缓存，提升效率
        return ReturnCodeEnum.SUCCESS;
    }

}