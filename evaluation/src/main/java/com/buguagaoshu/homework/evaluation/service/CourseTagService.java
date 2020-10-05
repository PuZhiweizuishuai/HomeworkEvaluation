package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.CourseTagEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 课程分类
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-08 18:44:35
 */
public interface CourseTagService extends IService<CourseTagEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 生成课程分类树
     *
     * @return
     */
    List<CourseTagEntity> listWithTree();


    /**
     * 返回分类标签哈希表
     *
     * @return 缓存结果
     */
    Map<Long, CourseTagEntity> CourseTagMap();


    /**
     * 更新标签
     *
     * @param courseTagEntity 新的标签数据
     * @param request         用户数据
     * @return 结果
     */
    boolean updateTag(CourseTagEntity courseTagEntity, HttpServletRequest request);


    /**
     * 创建新的标签
     *
     * @param courseTagEntity 标签
     * @return 结果
     */
    boolean saveTag(CourseTagEntity courseTagEntity);


    /**
     * 删除分类
     * @param tagId 分类ID
     * @return 删除结果
     * */
    boolean deleteTag(Long tagId, String verifyCode, HttpServletRequest request);
}

