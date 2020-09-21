package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;

import java.util.List;
import java.util.Map;

/**
 * 学生-课程关系列表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface StudentsCurriculumService extends IService<StudentsCurriculumEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 使用课程ID查找当前学生是否选择这门课程
     * 或在这门课程内
     * @param userId 学生ID
     * @param curriculumId 课程ID
     * @return 查找结果
     * */
    StudentsCurriculumEntity selectStudentByCurriculumId(String userId, Long curriculumId);


    /**
     * 通过课程ID查找这门课的其他老师
     * @param id 课程ID
     * @param teacher 班主任，班级创建者
     * @return 老师列表
     * */
    List<StudentsCurriculumEntity> teacherList(Long id, String teacher);


    /**
     * 通过课程ID查找这门课的所有老师，包括创建这门课的老师
     * @param id 课程ID
     * @return 老师列表
     * */
    List<StudentsCurriculumEntity> teacherList(Long id);


    /**
     * 检查老师是否是这门课的老师
     * @param classId 课程ID
     * @param teacherId 教师ID
     * @param homeworkEntity 作业信息
     * @return 结果
     * */
    boolean checkThisCurriculumHaveTeacher(long classId, String teacherId, HomeworkEntity homeworkEntity);


    /**
     * 检查老师是否是这门课的老师
     * @param classId 课程ID
     * @param teacherId 教师ID
     * @return 结果
     * */
    boolean checkThisCurriculumHaveTeacher(long classId, String teacherId);

    /**
     * 返回在班级内的学生数据
     * @param userEntityList 学生数据
     * @param id 课程 ID
     * @return 在班级内的学生数据
     * */
    List<UserEntity> findUserByIdAndCurriculumId(List<UserEntity> userEntityList, Long id);
}

