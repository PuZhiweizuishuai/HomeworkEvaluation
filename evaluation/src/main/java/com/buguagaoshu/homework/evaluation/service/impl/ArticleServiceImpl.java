package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.CustomPage;
import com.buguagaoshu.homework.common.enums.ArticleTypeEnum;
import com.buguagaoshu.homework.common.enums.AtUserTypeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.CurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.ArticleModel;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.ArticleVo;
import com.buguagaoshu.homework.evaluation.vo.DeleteVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Pu Zhiwei
 */
@Service("articleService")
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, ArticleEntity> implements ArticleService {
    private final StudentsCurriculumService studentsCurriculumService;

    private final UserService userService;

    private final VerifyCodeService verifyCodeService;

    private final AtUserService atUserService;

    private final CurriculumService curriculumService;

    @Autowired
    public ArticleServiceImpl(StudentsCurriculumService studentsCurriculumService, UserService userService, VerifyCodeService verifyCodeService, AtUserService atUserService, CurriculumService curriculumService) {
        this.studentsCurriculumService = studentsCurriculumService;
        this.userService = userService;
        this.verifyCodeService = verifyCodeService;
        this.atUserService = atUserService;
        this.curriculumService = curriculumService;
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
    @Transactional(rollbackFor = {})
    public ArticleEntity saveArticle(ArticleVo articleVo, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String verifyCodeKey = (String) session.getAttribute(WebConstant.VERIFY_CODE_KEY);
        verifyCodeService.verify(verifyCodeKey, articleVo.getVerifyCode());
        if (articleVo.getTag() != null && articleVo.getTag().size() > 6) {
            throw new UserDataFormatException("标签不能超过6个！");
        }
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.initData();
        if (articleVo.getCourseId() != null) {
            StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), articleVo.getCourseId());
            if (studentsCurriculumEntity == null) {
                return null;
            }
            // 检查评分
            if (articleVo.getType() == ArticleTypeEnum.COURSE_RATING.getCode()) {

                ArticleEntity courseRating = getCourseRating(articleVo.getCourseId(), user);
                if (courseRating != null) {
                    throw new UserDataFormatException("你已经评价过这门课程了！");
                }
                if (articleVo.getCourseRating() == null) {
                    throw new UserDataFormatException("评分有误！");
                }
                if (articleVo.getCourseRating() > 5.0  || articleVo.getCourseRating() < 0.0) {
                    throw new UserDataFormatException("评分有误！");
                }
                // TODO 放缓存
                CurriculumEntity curriculum = curriculumService.getById(articleVo.getCourseId());
                curriculum.setScore(articleVo.getCourseRating() + curriculum.getScore());
                curriculum.setRatingUserNumber(curriculum.getRatingUserNumber() + 1);
                curriculumService.updateById(curriculum);
            }
        }

        articleEntity.setAuthorId(user.getId());
        articleEntity.setAuthorName(user.getSubject());
        articleEntity.setIp(IpUtil.getIpAddr(request));
        articleEntity.setUa(IpUtil.getUa(request));
        BeanUtils.copyProperties(articleVo, articleEntity);

        //articleEntity.setTitle(HtmlUtils.htmlEscape(articleEntity.getTitle()));
        //articleEntity.setContent(HtmlUtils.htmlEscape(articleEntity.getContent()));


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (articleVo.getTag() != null && articleEntity.getTag().length() != 0) {
                articleEntity.setTag(objectMapper.writeValueAsString(articleVo.getTag()));
            } else {
                articleEntity.setTag("[]");
            }
        } catch (JsonProcessingException e) {
            log.error("用户 {} 发帖， 标签出现序列化异常！{}", user.getId(), e.getMessage());
        }
        this.save(articleEntity);

        String atUser = atUserService.atUser(articleVo.getAtUsers(), AtUserTypeEnum.ARTICLE_AT, articleEntity, null, user.getId(), user.getSubject());
        if (!"".equals(atUser)) {
            articleEntity.setAtUser(atUser);
            this.updateById(articleEntity);
        }
        return articleEntity;
    }

    @Override
    public ArticleEntity getCourseRating(Long courseId, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        return getCourseRating(courseId, user);
    }

    @Override
    public ArticleEntity getCourseRating(Long courseId, Claims user) {
        QueryWrapper<ArticleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        wrapper.eq("type", ArticleTypeEnum.COURSE_RATING.getCode());
        wrapper.eq("author_id", user.getId());
        wrapper.ne("status", ArticleTypeEnum.DELETE.getCode());
        return this.getOne(wrapper);
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
        wrapper.ne("status", ArticleTypeEnum.DELETE.getCode());
        wrapper.eq("type", ArticleTypeEnum.COURSE.getCode());
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.like("title", key);
        }
        String sort = (String) params.get("sort");
        if (!StringUtils.isEmpty(sort)) {
            switch (sort) {
                case "0":
                    wrapper.orderByDesc("update_time");
                    break;
                case "1":
                    wrapper.orderByDesc("latest_comment_time");
                    break;
                case "2":
                    wrapper.eq("perfect", 1);
                    wrapper.orderByDesc("update_time");
                    break;
                case "3":
                    wrapper.eq("comment_count", 0);
                    wrapper.orderByDesc("update_time");
                    break;
                default:
                    wrapper.orderByDesc("update_time");
                    break;
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
        Map<String, UserEntity> maps = userService.listByIds(userId).stream().collect(Collectors.toMap(UserEntity::getUserId, u -> u));
        List<ArticleModel> articleModels = new ArrayList<>();
        Map<String, StudentsCurriculumEntity> teacherMap = studentsCurriculumService.teacherList(courseId).stream().collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, t -> t));


        page.getRecords().forEach((a) -> {
            ArticleModel articleModel = new ArticleModel();
            BeanUtils.copyProperties(a, articleModel);
            articleModel.setContent(null);
            articleModel.setAvatarUrl(maps.get(a.getAuthorId()).getUserAvatarUrl());
            articleModel.setIsTeacher(teacherMap.get(a.getAuthorId()) != null);
            articleModels.add(articleModel);
        });
        return new PageUtils(new CustomPage<>(articleModels, page.getTotal(), page.getSize(), page.getCurrent(), page.orders()));
    }

    @Override
    public ArticleModel courseArticleInfo(Long id, HttpServletRequest request) {
        ArticleEntity articleEntity = this.getById(id);
        if (articleEntity == null) {
            return null;
        }
        if (articleEntity.getType() == ArticleTypeEnum.COURSE.getCode()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
                StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), articleEntity.getCourseId());
                ArticleModel articleModel = new ArticleModel();
                if (studentsCurriculumEntity != null) {
                    UserEntity userEntity = userService.getById(articleEntity.getAuthorId());
                    BeanUtils.copyProperties(articleEntity, articleModel);
                    articleModel.setAuthorName(userEntity.getUsername());
                    articleModel.setAvatarUrl(userEntity.getUserAvatarUrl());
                    Map<String, StudentsCurriculumEntity> teacherMap = studentsCurriculumService.teacherList(articleEntity.getCourseId()).stream().collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, t -> t));
                    articleModel.setIsTeacher(teacherMap.get(articleEntity.getAuthorId()) != null);
                    try {
                        articleModel.setTag((List<String>) objectMapper.readValue(articleEntity.getTag(), List.class));
                    } catch (Exception ignored) {
                    }
                    articleEntity.setViewCount(articleEntity.getViewCount()+1);
                    // TODO Redis 缓存
                    this.baseMapper.countAdd("view_count", articleEntity.getId(), 1);
                    return articleModel;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void countNumberCount(String col, Long articleId, Integer count) {
        this.baseMapper.countAdd(col, articleId, count);
    }

    @Override
    public boolean deleteArticle(DeleteVo deleteVo, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        ArticleEntity byId = getById(deleteVo.getId());
        if (byId != null) {
            if (byId.getAuthorId().equals(user.getId())) {
                byId.setStatus(ArticleTypeEnum.DELETE.getCode());
                this.updateById(byId);
                return true;
            }
            if (user.get("authorities").equals(RoleTypeEnum.ADMIN.getRole())) {
                byId.setStatus(ArticleTypeEnum.DELETE.getCode());
                this.updateById(byId);
                return true;
            }
            if (byId.getType().equals(ArticleTypeEnum.COURSE.getCode())) {
                List<StudentsCurriculumEntity> studentsCurriculumEntities = studentsCurriculumService.teacherList(byId.getCourseId());
                for (StudentsCurriculumEntity s : studentsCurriculumEntities) {
                    if (s.getStudentId().equals(user.getId())) {
                        byId.setStatus(1);
                        this.updateById(byId);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean perfect(DeleteVo deleteVo, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        ArticleEntity articleEntity = getById(deleteVo.getId());
        if (articleEntity != null) {
            if (articleEntity.getPerfect() == 0) {
                articleEntity.setPerfect(1);
            } else {
                articleEntity.setPerfect(0);
            }
            if (user.get("authorities").equals(RoleTypeEnum.ADMIN.getRole())) {
                this.updateById(articleEntity);
                return true;
            }
            if (articleEntity.getType().equals(ArticleTypeEnum.COURSE.getCode())) {
                List<StudentsCurriculumEntity> studentsCurriculumEntities = studentsCurriculumService.teacherList(articleEntity.getCourseId());
                for (StudentsCurriculumEntity s : studentsCurriculumEntities) {
                    if (s.getStudentId().equals(user.getId())) {
                        this.updateById(articleEntity);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public PageUtils courseRating(Long courseId, Map<String, Object> params) {
        QueryWrapper<ArticleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        wrapper.ne("status", ArticleTypeEnum.DELETE.getCode());
        wrapper.eq("type", ArticleTypeEnum.COURSE_RATING.getCode());
        String sort = (String) params.get("sort");
        if (!StringUtils.isEmpty(sort)) {
            // TODO 细节优化
            switch (sort) {
                case "0":
                    wrapper.orderByDesc("like_count");
                    break;
                case "1":
                    wrapper.orderByDesc("update_time");
                    break;
                case "2":
                    break;
                default:
                    wrapper.orderByDesc("update_time");
            }
        }
        IPage<ArticleEntity> page = this.page(
                new Query<ArticleEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public ArticleEntity updateUserCourseRating(ArticleVo articleVo, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        verifyCodeService.verify((String) request.getSession().getAttribute(WebConstant.VERIFY_CODE_KEY), articleVo.getVerifyCode());
        StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), articleVo.getCourseId());
        if (studentsCurriculumEntity == null) {
            return null;
        }
        if (articleVo.getCourseRating() == null) {
            throw new UserDataFormatException("评分有误！");
        }
        if (articleVo.getCourseRating() > 5.0  || articleVo.getCourseRating() < 0.0) {
            throw new UserDataFormatException("评分有误！");
        }
        ArticleEntity courseRating = getCourseRating(articleVo.getCourseId(), user);
        if (courseRating == null) {
            return null;
        }
        // 计算分数
        if (!courseRating.getCourseRating().equals(articleVo.getCourseRating())) {
            curriculumService.addCount("score", courseRating.getCourseId(), articleVo.getCourseRating() - courseRating.getCourseRating());
        }
        courseRating.setTitle(articleVo.getTitle());
        courseRating.setContent(articleVo.getContent());
        courseRating.setCourseRating(articleVo.getCourseRating());
        courseRating.setUpdateTime(System.currentTimeMillis());
        courseRating.setIp(IpUtil.getIpAddr(request));
        courseRating.setUa(IpUtil.getUa(request));

        this.updateById(courseRating);
        return courseRating;
    }
}