package com.buguagaoshu.homework.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 学生-课程关系列表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
@Data
@TableName("students_curriculum")
public class StudentsCurriculumEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 学生学号
     */
    private String studentId;
    /**
     * 学生加入的课程ID
     */
    private Long curriculumId;
    /**
     * 加入时间
     */
    private Long createTime;

    /**
     * 在课程内的角色
     * */
    private String role;

}
