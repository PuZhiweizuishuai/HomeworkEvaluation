package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.utils.FileUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.CoursewareDao;
import com.buguagaoshu.homework.evaluation.entity.CoursewareEntity;
import com.buguagaoshu.homework.evaluation.service.CoursewareService;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pu Zhiwei
 * */
@Service("coursewareService")
public class CoursewareServiceImpl extends ServiceImpl<CoursewareDao, CoursewareEntity> implements CoursewareService {

    private final StudentsCurriculumService studentsCurriculumService;


    private final FileUtil fileUtil;

    @Autowired
    public CoursewareServiceImpl(StudentsCurriculumService studentsCurriculumService, FileUtil fileUtil) {
        this.studentsCurriculumService = studentsCurriculumService;
        this.fileUtil = fileUtil;
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CoursewareEntity> page = this.page(
                new Query<CoursewareEntity>().getPage(params),
                new QueryWrapper<CoursewareEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CoursewareEntity> coursewareTree(Long courseId, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        StudentsCurriculumEntity student =
                studentsCurriculumService.selectStudentByCurriculumId(user.getId(), courseId);
        if (student == null) {
            return null;
        }
        QueryWrapper<CoursewareEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        List<CoursewareEntity> list = this.list(wrapper);
        List<CoursewareEntity> levelTree =
                list.stream().filter((categoryEntity -> categoryEntity.getLevel() == 0))
                        .peek((courseware) -> courseware.setChildren(getChildren(courseware, list)))
                        .sorted(Comparator.comparingInt(tag -> (tag.getSort() == null ? 0 : tag.getSort())))
                        .collect(Collectors.toList());
        return levelTree;
    }

    @Override
    public boolean saveCourseware(CoursewareEntity coursewareEntity, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        StudentsCurriculumEntity student =
                studentsCurriculumService.selectStudentByCurriculumId(user.getId(), coursewareEntity.getCourseId());
        if (student == null) {
            return false;
        }
        if (student.getRole().equals(RoleTypeEnum.TEACHER.getRole())) {
            long time = System.currentTimeMillis();

            coursewareEntity.setCreateTeacher(user.getId());
            coursewareEntity.setCaretTime(time);
            coursewareEntity.setUpdateTime(time);
            if (coursewareEntity.getLevel() == 0) {
                coursewareEntity.setFatherId(0L);
            }
            // 设置文件格式
            if (!StringUtils.isEmpty(coursewareEntity.getFileUrl())) {
                coursewareEntity.setFileType(fileUtil.fileTypeCode(coursewareEntity.getFileUrl()));
            }
            this.save(coursewareEntity);
            return true;
        }
        return false;
    }


    private List<CoursewareEntity> getChildren(CoursewareEntity root,
                                              List<CoursewareEntity> all) {
        List<CoursewareEntity> children = all.stream().filter((categoryEntity) -> categoryEntity.getFatherId().equals(root.getId()))
                .peek((courseTagEntity) -> {
                    // 查找子分类
                    courseTagEntity.setChildren(getChildren(courseTagEntity, all));
                })
                .sorted(Comparator.comparingInt(tag -> (tag.getSort() == null ? 0 : tag.getSort())))
                .collect(Collectors.toList());
        return children;
    }

}