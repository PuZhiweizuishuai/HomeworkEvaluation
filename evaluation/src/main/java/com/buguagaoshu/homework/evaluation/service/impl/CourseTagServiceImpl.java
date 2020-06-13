package com.buguagaoshu.homework.evaluation.service.impl;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.CourseTagDao;
import com.buguagaoshu.homework.evaluation.entity.CourseTagEntity;
import com.buguagaoshu.homework.evaluation.service.CourseTagService;


/**
 * @author puzhiwei
 */
@Service("courseTagService")
public class CourseTagServiceImpl extends ServiceImpl<CourseTagDao, CourseTagEntity> implements CourseTagService {

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
