package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.StudentsCurriculumDao;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;


/**
 * @author puzhiwei
 */
@Service("studentsCurriculumService")
public class StudentsCurriculumServiceImpl extends ServiceImpl<StudentsCurriculumDao, StudentsCurriculumEntity> implements StudentsCurriculumService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StudentsCurriculumEntity> page = this.page(
                new Query<StudentsCurriculumEntity>().getPage(params),
                new QueryWrapper<StudentsCurriculumEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public StudentsCurriculumEntity selectStudentByCurriculumId(String userId, Long curriculumId) {
        QueryWrapper<StudentsCurriculumEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", userId);
        wrapper.eq("curriculum_id", curriculumId);
        return this.getOne(wrapper);
    }

    @Override
    public List<StudentsCurriculumEntity> teacherList(Long id, String teacher) {
        QueryWrapper<StudentsCurriculumEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("curriculum_id", id);
        wrapper.eq("role", RoleTypeEnum.TEACHER.getRole());
        return this.list(wrapper).stream().filter((t)->{
            return !t.getStudentId().equals(teacher);
        }).collect(Collectors.toList());

    }

    @Override
    public List<StudentsCurriculumEntity> teacherList(Long id) {
        QueryWrapper<StudentsCurriculumEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("curriculum_id", id);
        wrapper.eq("role", RoleTypeEnum.TEACHER.getRole());
        return this.list(wrapper);

    }

    @Override
    public List<UserEntity> findUserByIdAndCurriculumId(List<UserEntity> userEntityList, Long id) {
        List<String> ids =
                userEntityList.stream().map((u)->{
                    u.setPassword("");
                    return u.getUserId();
                }).collect(Collectors.toList());
        QueryWrapper<StudentsCurriculumEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("curriculum_id", id);
        wrapper.in("student_id", ids);
        List<StudentsCurriculumEntity> studentsCurriculumEntities
                = this.list(wrapper);
        Map<String, StudentsCurriculumEntity> map = new HashMap<>();
        if (studentsCurriculumEntities != null && studentsCurriculumEntities.size() != 0) {
            studentsCurriculumEntities.forEach((u)->{
                map.put(u.getStudentId(), u);
            });
            List<UserEntity> userEntities = userEntityList.stream().filter((u)->{
                // 补充班级内权限
                u.setRole(map.get(u.getUserId()).getRole());
                return map.get(u.getUserId()) != null;
            }).collect(Collectors.toList());
            return userEntities;
        }
        return null;
    }

}
