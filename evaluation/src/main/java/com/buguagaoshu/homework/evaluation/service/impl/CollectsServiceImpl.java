package com.buguagaoshu.homework.evaluation.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.buguagaoshu.homework.common.enums.ArticleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.service.ArticleService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.CollectsDao;
import com.buguagaoshu.homework.evaluation.entity.CollectsEntity;
import com.buguagaoshu.homework.evaluation.service.CollectsService;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


@Service("collectsService")
public class CollectsServiceImpl extends ServiceImpl<CollectsDao, CollectsEntity> implements CollectsService {

    private ArticleService articleService;

    private final StudentsCurriculumService studentsCurriculumService;

    @Autowired
    public CollectsServiceImpl(StudentsCurriculumService studentsCurriculumService) {
        this.studentsCurriculumService = studentsCurriculumService;
    }

    @Autowired
    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, HttpServletRequest request) {
        Claims user = null;
        try {
            user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        } catch (Exception ignored) {}
        QueryWrapper<CollectsEntity> wrapper = new QueryWrapper<>();
        String userId = (String) params.get("user");
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        wrapper.eq("user_id", userId);
        String type = (String) params.get("type");
        int min = ArticleTypeEnum.ORDINARY.getCode();
        int max = ArticleTypeEnum.ORDINARY_END.getCode();
        boolean flag = true;
        if (!StringUtils.isEmpty(type)) {
            if (type.equals("2")) {
                if (user == null) {
                    return null;
                }
                if (!user.getId().equals(userId)) {
                    return null;
                }
                min = ArticleTypeEnum.COURSE.getCode();
                max = ArticleTypeEnum.COURSE_END.getCode();
            } else if (type.equals("1")) {
                flag = false;
                wrapper.eq("type", ArticleTypeEnum.THINK.getCode());
            }
        }
        if (flag) {
            wrapper.ge("type", min);
            wrapper.le("type", max);
        }

        IPage<CollectsEntity> page = this.page(
                new Query<CollectsEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public CollectsEntity save(CollectsEntity collectsEntity, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        CollectsEntity collects = findCollectsByUserIdWithArticleId(user.getId(), collectsEntity.getArticleId());
        if (collects != null) {
            throw new UserDataFormatException("已经收藏过了！");
        }
        ArticleEntity article = articleService.getById(collectsEntity.getArticleId());
        if (article == null) {
            throw new UserDataFormatException("帖子不存在或已经被删除！");
        }
        if (article.getStatus() == ArticleTypeEnum.DELETE.getCode()) {
            throw new UserDataFormatException("帖子不存在或已经被删除！");
        }
        // 课程内
        if (article.getCourseId() != null) {
            StudentsCurriculumEntity student = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), article.getCourseId());
            if (student == null) {
                throw new UserDataFormatException("没有权限！");
            }
            collectsEntity.setCourseId(article.getCourseId());
        }
        collectsEntity.setId(IdWorker.getId());
        collectsEntity.setCreateTime(System.currentTimeMillis());
        collectsEntity.setText(article.getTitle());
        collectsEntity.setUserId(user.getId());
        collectsEntity.setType(article.getType());
        collectsEntity.setIp(IpUtil.getIpAddr(request));
        collectsEntity.setUa(IpUtil.getUa(request));
        this.save(collectsEntity);
        articleService.countNumberCount("collect_count", article.getId(), 1);
        return collectsEntity;
    }

    @Override
    public CollectsEntity findCollectsByUserIdWithArticleId(String userId, Long article) {
        QueryWrapper<CollectsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("article_id", article);
        return this.getOne(wrapper);
    }


}