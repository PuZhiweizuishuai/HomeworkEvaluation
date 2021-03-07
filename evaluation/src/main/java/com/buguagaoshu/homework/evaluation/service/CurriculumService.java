package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.CurriculumEntity;
import com.buguagaoshu.homework.evaluation.model.CurriculumModel;
import com.buguagaoshu.homework.evaluation.model.JoinCourseCode;
import com.buguagaoshu.homework.evaluation.vo.CurriculumIndexVo;
import com.buguagaoshu.homework.evaluation.vo.CurriculumInfo;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 课程
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface CurriculumService extends IService<CurriculumEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 创建课程
     * @param curriculumModel 课程模型
     * @param teacher 创建老师
     * @return 创建完成的课程
     * */
    CurriculumEntity createCurriculum(CurriculumModel curriculumModel, Claims teacher);



    /**
     * 更新课程
     * @param curriculumModel 课程模型
     * @param request 获取教师信息
     * @return 更新完成的课程
     * */
    CurriculumEntity updateCurriculum(CurriculumModel curriculumModel, HttpServletRequest request);


    /**
     * 查用户课程列表
     * 包括老师自己创建的和学生加入的
     * 公开 API，任何人均可查找
     * 因为需要在首页显示
     * @param params 查询参数
     *               teacher 查询老师的 ID
     *               user 用户ID
     *               page 页码
     *               limit 每页数量
     *               tag 标签
     * @param request 请求查询的用户信息
     * @return 分页查找的结果
     * */
    PageUtils selectUserCurriculumList(Map<String, Object> params, HttpServletRequest request);

    /**
     * 当前用户加入的课程列表
     * @param userId ID
     * @param request 请求查询的用户信息
     * @return 分页查找的结果
     * */
    PageUtils selectJoinCurriculumList(String userId, HttpServletRequest request);



    /**
     * 查找当前这门课程的基本信息
     * @param id 课程ID
     * @return 详细信息
     * */
    CurriculumInfo info(Long id);

    /**
     * 发送加入课程请求
     * @param id 课程 ID
     * @param request 获取请求用户
     * @param code 密码
     * @return 结果
     * */
    ReturnCodeEnum join(Long id, HttpServletRequest request, JoinCourseCode code);


    /**
     * 返回课程学习页需要的信息
     * @param id 课程ID
     * @param request 获取用户信息
     * @return map： {
     *     course： CurriculumEntity
     *     user： StudentsCurriculumEntity
     * }
     * */
    Map<String, Object> learn(Long id, HttpServletRequest request);



    void addCount(String col, Long id, Integer count);


    void addCount(String col, Long id, Double count);
}

