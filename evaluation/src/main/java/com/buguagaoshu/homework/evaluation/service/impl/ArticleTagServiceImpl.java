package com.buguagaoshu.homework.evaluation.service.impl;


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

import com.buguagaoshu.homework.evaluation.dao.ArticleTagDao;
import com.buguagaoshu.homework.evaluation.entity.ArticleTagEntity;
import com.buguagaoshu.homework.evaluation.service.ArticleTagService;

/**
 * @author Pu Zhiwei
 */
@Service("articleTagService")
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagDao, ArticleTagEntity> implements ArticleTagService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ArticleTagEntity> page = this.page(
                new Query<ArticleTagEntity>().getPage(params),
                new QueryWrapper<ArticleTagEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ArticleTagEntity> listTree() {
        List<ArticleTagEntity> list = baseMapper.selectList(null);

        // 组装成父子的树形结构
        List<ArticleTagEntity> level1Menus =
                list.stream().filter((categoryEntity -> categoryEntity.getCatelogId() == 0L))
                        .peek((menu) -> menu.setChildren(getChildren(menu, list)))
                        .sorted(Comparator.comparingInt(tag -> (tag.getSort() == null ? 0 : tag.getSort())))
                        .collect(Collectors.toList());
        return level1Menus;

    }

    @Override
    public Map<Long, ArticleTagEntity> listToMap() {
        return baseMapper.selectList(null).stream().collect(Collectors.toMap(ArticleTagEntity::getId, t -> t));
    }

    private List<ArticleTagEntity> getChildren(ArticleTagEntity root,
                                               List<ArticleTagEntity> all) {
        List<ArticleTagEntity> children = all.stream().filter((categoryEntity) -> categoryEntity.getCatelogId().equals(root.getId()))
                .peek((courseTagEntity) -> {
                    // 查找子分类
                    courseTagEntity.setChildren(getChildren(courseTagEntity, all));
                })
                .sorted(Comparator.comparingInt(tag -> (tag.getSort() == null ? 0 : tag.getSort())))
                .collect(Collectors.toList());
        return children;
    }

}