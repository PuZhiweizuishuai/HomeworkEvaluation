package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.EvaluationType;
import com.buguagaoshu.homework.evaluation.entity.CurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.CurriculumService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.HomeworkDao;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.service.HomeworkService;


/**
 * @author puzhiwei
 */
@Service("homeworkService")
public class HomeworkServiceImpl extends ServiceImpl<HomeworkDao, HomeworkEntity> implements HomeworkService {

    private CurriculumService curriculumService;

    private StudentsCurriculumService studentsCurriculumService;

    @Autowired
    public void setCurriculumService(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @Autowired
    public void setStudentsCurriculumService(StudentsCurriculumService studentsCurriculumService) {
        this.studentsCurriculumService = studentsCurriculumService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HomeworkEntity> page = this.page(
                new Query<HomeworkEntity>().getPage(params),
                new QueryWrapper<HomeworkEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public HomeworkEntity add(HomeworkEntity homeworkEntity, Claims nowLoginUser) {
        CurriculumEntity curriculumEntity = curriculumService.getById(homeworkEntity.getClassNumber());
        if (curriculumEntity != null) {
            if (curriculumEntity.getCreateTeacher().equals(nowLoginUser.getId())) {
                // TODO 作业创建成功后通知所有班级成员
                return saveHomework(homeworkEntity, nowLoginUser);
            } else {
                StudentsCurriculumEntity studentsCurriculumEntity
                        = studentsCurriculumService.selectStudentByCurriculumId(nowLoginUser.getId(), curriculumEntity.getId());
                if (studentsCurriculumEntity != null) {
                    return saveHomework(homeworkEntity, nowLoginUser);
                }
                return null;
            }
        }
        return null;
    }


    private HomeworkEntity saveHomework(HomeworkEntity homeworkEntity, Claims teacher) {
        homeworkEntity.setCreateTeacher(teacher.getId());
        homeworkEntity.setCreateTime(System.currentTimeMillis());
        homeworkEntity.setSubmitCount(0);
        // 添加作业的初始阶段是未发布状态
        // 添加题目后到发布状态
        // 然后再计算时间
        homeworkEntity.setStatus(EvaluationType.HOMEWORK_NO_PUBLISH.getCode());
//        long time = System.currentTimeMillis();
//        if (homeworkEntity.getOpenTime() >= time && homeworkEntity.getCloseTime() <= time) {
//            homeworkEntity.setStatus(EvaluationType.EVALUATION_NO_START.getCode());
//        } else {
//            homeworkEntity.setStatus(EvaluationType.HOMEWORK_NO_START.getCode());
//        }

        this.save(homeworkEntity);
        return homeworkEntity;
    }

}
