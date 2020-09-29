package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.ArticleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.service.VerifyCodeService;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.ArticleVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.ArticleDao;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.service.ArticleService;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Pu Zhiwei
 * */
@Service("articleService")
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, ArticleEntity> implements ArticleService {
    private final StudentsCurriculumService studentsCurriculumService;

    private final UserService userService;

    private final VerifyCodeService verifyCodeService;

    @Autowired
    public ArticleServiceImpl(StudentsCurriculumService studentsCurriculumService, UserService userService, VerifyCodeService verifyCodeService) {
        this.studentsCurriculumService = studentsCurriculumService;
        this.userService = userService;
        this.verifyCodeService = verifyCodeService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ArticleEntity> page = this.page(
                new Query<ArticleEntity>().getPage(params),
                new QueryWrapper<ArticleEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public ArticleEntity saveArticle(ArticleVo articleVo, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String verifyCodeKey = (String) session.getAttribute("verifyCodeKey");
        verifyCodeService.verify(verifyCodeKey, articleVo.getVerifyCode());
        if (articleVo.getTag() != null && articleVo.getTag().size() > 6) {
            throw new UserDataFormatException("标签不能超过6个！");
        }
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        ArticleEntity articleEntity = new ArticleEntity();
        initArticleEntity(articleEntity);
        if (articleVo.getCourseId() != null) {
            StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), articleVo.getCourseId());
            if (studentsCurriculumEntity == null) {
                return null;
            }
            // 检查评分
            if (articleVo.getType() == ArticleTypeEnum.COURSE_RATING.getCode()) {
                if (articleVo.getCourseRating() == null) {
                    throw new UserDataFormatException("评分有误！");
                }
                if (articleVo.getCourseRating() > 10.0) {
                    throw new UserDataFormatException("评分有误！");
                }
            }
        }

        articleEntity.setAuthorId(user.getId());
        articleEntity.setAuthorName(user.getSubject());
        articleEntity.setIp(IpUtil.getIpAddr(request));
        articleEntity.setUa(request.getHeader("user-agent"));
        BeanUtils.copyProperties(articleVo, articleEntity);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            articleEntity.setTag(objectMapper.writeValueAsString(articleVo.getTag()));
        } catch (JsonProcessingException e) {
            log.error("用户 {} 发帖， 标签出现序列化异常！{}",user.getId(), e.getMessage());
        }
        this.save(articleEntity);
        return articleEntity;
    }

    @Override
    public PageUtils getCourseList(Long courseId, Map<String, Object> params, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), courseId);
        if (studentsCurriculumEntity == null) {
            return null;
        }
        QueryWrapper<ArticleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        wrapper.eq("type", ArticleTypeEnum.COURSE.getCode());
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.like("title", key);
        }
        String sort = (String) params.get("sort");
        if (!StringUtils.isEmpty(sort)) {
            if ("0".equals(sort)) {
                wrapper.orderByDesc("update_time");
            } else if ("1".equals(sort)) {
                wrapper.orderByDesc("latest_comment_time");
            } else {
                wrapper.orderByDesc("update_time");
            }
            // TODO 热门待更新
        } else {
            wrapper.orderByDesc("update_time");
        }
        IPage<ArticleEntity> page = this.page(
                new Query<ArticleEntity>().getPage(params),
                wrapper
        );
        if (page.getTotal() == 0) {
            return new PageUtils(page);
        }
        Set<String> userId = page.getRecords().stream().map(ArticleEntity::getAuthorId).collect(Collectors.toSet());
        Map<String, UserEntity> maps = userService.listByIds(userId).stream().collect(Collectors.toMap(UserEntity::getUserId, u->u));
        page.getRecords().forEach((a) -> {
            a.setAvatarUrl(maps.get(a.getAuthorId()).getUserAvatarUrl());
            a.setContent("");
        });
        return new PageUtils(page);
    }


    public void initArticleEntity(ArticleEntity articleEntity) {
        long time = System.currentTimeMillis();
        articleEntity.setCommentCount(0L);
        articleEntity.setViewCount(0L);
        articleEntity.setCreateTime(time);
        articleEntity.setUpdateTime(time);
        articleEntity.setLatestCommentName(null);
        articleEntity.setLatestCommentTime(null);
        articleEntity.setStatus(0);
        articleEntity.setLikeCount(0L);
        articleEntity.setBadCount(0L);
        articleEntity.setCollectCount(0L);
        articleEntity.setArticlestick(0L);
        articleEntity.setAnonymous(0);
        articleEntity.setPerfect(0);
        articleEntity.setQAOfferPoint(null);
    }
}