package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.evaluation.cache.CourseTagCache;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.service.VerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.CourseTagDao;
import com.buguagaoshu.homework.evaluation.entity.CourseTagEntity;
import com.buguagaoshu.homework.evaluation.service.CourseTagService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * @author puzhiwei
 */
@Service("courseTagService")
public class CourseTagServiceImpl extends ServiceImpl<CourseTagDao, CourseTagEntity> implements CourseTagService {

    private final CourseTagCache courseTagCache;

    private final VerifyCodeService verifyCodeService;

    @Autowired
    public CourseTagServiceImpl(CourseTagCache courseTagCache, VerifyCodeService verifyCodeService) {
        this.courseTagCache = courseTagCache;
        this.verifyCodeService = verifyCodeService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CourseTagEntity> page = this.page(
                new Query<CourseTagEntity>().getPage(params),
                new QueryWrapper<CourseTagEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CourseTagEntity> listWithTree() {
        // 获取所有的分类
        List<CourseTagEntity> list = baseMapper.selectList(null);

        // 组装成父子的树形结构
        List<CourseTagEntity> level1Menus =
                list.stream().filter((categoryEntity -> categoryEntity.getCatelogId() == 0))
                        .peek((menu) -> menu.setChildren(getChildren(menu, list)))
                        .sorted(Comparator.comparingInt(tag -> (tag.getSort() == null ? 0 : tag.getSort())))
                        .collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public Map<Long, CourseTagEntity> CourseTagMap() {
        List<CourseTagEntity> list = baseMapper.selectList(null);
        Map<Long, CourseTagEntity> map = new HashMap<>(list.size());
        list.forEach((c)->{
            map.put(c.getId(), c);
        });
        return map;
    }

    @Override
    public boolean updateTag(CourseTagEntity courseTagEntity, HttpServletRequest request) {
        CourseTagEntity id = getById(courseTagEntity.getId());
        if (id == null) {
            return false;
        }
        if (id.getCatelogId() != 0) {
            CourseTagEntity father = getById(courseTagEntity.getId());
            if (father == null) {
                return false;
            }
        }
        this.updateById(courseTagEntity);
        updateCache();
        // 更新缓存
        return true;
    }

    public void updateCache() {
        List<CourseTagEntity> list = listWithTree();
        courseTagCache.setCourseTagListTree(list);
        courseTagCache.setCourseTagEntityMap(CourseTagMap());
        courseTagCache.setCourseTagMapHaveChildren(list);
    }

    @Override
    public boolean saveTag(CourseTagEntity courseTagEntity) {
        if (courseTagEntity.getCatelogId() == null) {
            courseTagEntity.setCatelogId(0L);
        }
        if (courseTagEntity.getCatelogId() != 0) {
            CourseTagEntity tagEntity = getById(courseTagEntity.getCatelogId());
            if (tagEntity == null) {
                return false;
            }
        }
        this.save(courseTagEntity);
        updateCache();
        return true;
    }

    @Override
    @Transactional(rollbackFor = {})
    public boolean deleteTag(Long tagId, String verifyCode, HttpServletRequest request) {
        verifyCodeService.verify(WebConstant.VERIFY_CODE_KEY, verifyCode, request.getSession());
        CourseTagEntity id = getById(tagId);
        if (id == null) {
            return false;
        }
        if (id.getCatelogId() == 0) {
            List<CourseTagEntity> list = list(new QueryWrapper<CourseTagEntity>().eq("catelog_id", id.getId()));
            if (list != null && list.size() != 0) {
                Set<Long> ids = list.stream().map(CourseTagEntity::getId).collect(Collectors.toSet());
                this.baseMapper.deleteBatchIds(ids);
            }
        }
        this.baseMapper.deleteById(id.getId());
        updateCache();
        return true;
    }

    /**
     * 获取某一菜单的子菜单
     */
    private List<CourseTagEntity> getChildren(CourseTagEntity root,
                                             List<CourseTagEntity> all) {
        List<CourseTagEntity> children = all.stream().filter((categoryEntity) -> categoryEntity.getCatelogId().equals(root.getId()))
                .peek((courseTagEntity) -> {
                    // 查找子分类
                    courseTagEntity.setChildren(getChildren(courseTagEntity, all));
                })
                .sorted(Comparator.comparingInt(tag -> (tag.getSort() == null ? 0 : tag.getSort())))
                .collect(Collectors.toList());
        return children;
    }
}
