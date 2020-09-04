package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import com.buguagaoshu.homework.evaluation.entity.SubmitHomeworkStatusEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-04 14:23
 * 教师在进行评价与批改时的主页所需要显示的当前作业数据
 */
@Data
public class KeeperDashboardViewVo {
    /**
     * 是否有显示权限
     */
    private Boolean showPower;

    /**
     * 作业信息
     */
    private HomeworkEntity homework;

    /**
     * 班级学生数
     */
    private Integer studentCount;


    /**
     * 点评批改数量
     * */
    private Integer commentCount;


    /**
     * 作业提交情况
     * */
    private List<SubmitHomeworkStatusEntity> submitList;

}
