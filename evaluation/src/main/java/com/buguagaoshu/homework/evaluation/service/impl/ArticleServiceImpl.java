package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.CustomPage;
import com.buguagaoshu.homework.common.enums.ArticleTypeEnum;
import com.buguagaoshu.homework.common.enums.AtUserTypeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.ArticleModel;
import com.buguagaoshu.homework.evaluation.model.VoteMode;
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

    private final ObjectMapper objectMapper;

    private final UserService userService;

    private final VoteService voteService;

    private final VerifyCodeService verifyCodeService;

    private final AtUserService atUserService;

    private final CurriculumService curriculumService;

    @Autowired
    public ArticleServiceImpl(StudentsCurriculumService studentsCurriculumService, ObjectMapper objectMapper, UserService userService, VoteService voteService, VerifyCodeService verifyCodeService, AtUserService atUserService, CurriculumService curriculumService) {
        this.studentsCurriculumService = studentsCurriculumService;
        this.objectMapper = objectMapper;

        this.userService = userService;
        this.voteService = voteService;
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
        // 处理课程内帖子
        if (articleVo.getCourseId() != null) {
            StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), articleVo.getCourseId());
            if (studentsCurriculumEntity == null) {
                return null;
            }
            if (!ArticleTypeEnum.checkCourseArticle(articleVo.getType())) {
                throw new UserDataFormatException("课程内帖子类型设置错误！");
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
                if (articleVo.getCourseRating() > 5.0 || articleVo.getCourseRating() < 0.0) {
                    throw new UserDataFormatException("评分有误！");
                }

                // TODO 放缓存
                CurriculumEntity curriculum = curriculumService.getById(articleVo.getCourseId());
                curriculum.setScore(articleVo.getCourseRating() + curriculum.getScore());
                curriculum.setRatingUserNumber(curriculum.getRatingUserNumber() + 1);
                curriculumService.updateById(curriculum);
            }
        }
        // 补全数据
        articleEntity.setAuthorId(user.getId());
        articleEntity.setAuthorName(user.getSubject());
        articleEntity.setIp(IpUtil.getIpAddr(request));
        articleEntity.setUa(IpUtil.getUa(request));
        BeanUtils.copyProperties(articleVo, articleEntity);
        // TODO 处理悬赏积分
        articleEntity.setQAOfferPoint(articleVo.getOfferPoint());

        // 序列化标签
        try {
            if (articleVo.getTag() != null && articleVo.getTag().size() != 0) {
                articleEntity.setTag(objectMapper.writeValueAsString(articleVo.getTag()));
            } else {
                articleEntity.setTag("[]");
            }
        } catch (JsonProcessingException e) {
            log.error("用户 {} 发帖， 标签出现序列化异常！{}", user.getId(), e.getMessage());
        }
        // 处理简介
        if (articleVo.getSimpleContent() == null) {
            if (articleEntity.getContent().length() > 50) {
                articleEntity.setSimpleContent(articleEntity.getContent().substring(0, 50));
            } else {
                articleEntity.setSimpleContent(articleEntity.getContent());
            }
        }
        // 保存数据
        this.save(articleEntity);

        // 检查投票
        if (ArticleTypeEnum.isVote(articleVo.getType())) {
            voteService.save(articleVo.getVotes(), articleEntity.getId());
        }

        // 处理@用户
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
        wrapper.ge("type", ArticleTypeEnum.COURSE.getCode());
        wrapper.le("type", ArticleTypeEnum.COURSE_END.getCode());
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
        // 如果是课程内的帖子
        if (ArticleTypeEnum.checkCourseArticle(articleEntity.getType())) {
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
                    userEntity.clean();
                    articleModel.setUser(userEntity);
                    Map<String, StudentsCurriculumEntity> teacherMap = studentsCurriculumService.teacherList(articleEntity.getCourseId()).stream().collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, t -> t));
                    articleModel.setIsTeacher(teacherMap.get(articleEntity.getAuthorId()) != null);
                    try {
                        articleModel.setTag((List<String>) objectMapper.readValue(articleEntity.getTag(), List.class));
                    } catch (Exception ignored) {
                    }
                    if (ArticleTypeEnum.isVote(articleEntity.getType())) {
                        articleModel.setVotes(voteService.getVoteModeList(articleEntity.getId()));
                        articleModel.setVoteLog(voteService.voteLogEntity(articleEntity.getId(), user.getId()));
                    }
                    articleEntity.setViewCount(articleEntity.getViewCount() + 1);
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
            // 课程内加精判断
            if (ArticleTypeEnum.checkCourseArticle(articleEntity.getType())) {
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
        if (articleVo.getCourseRating() > 5.0 || articleVo.getCourseRating() < 0.0) {
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

    @Override
    public PageUtils getArticleList(Map<String, Object> params, HttpServletRequest request) {
        QueryWrapper<ArticleEntity> wrapper = new QueryWrapper<>();
        wrapper.ne("status", ArticleTypeEnum.DELETE.getCode());

        String userId = (String) params.get("user");
        wrapper.ge("type", ArticleTypeEnum.ORDINARY.getCode());
        wrapper.le("type", ArticleTypeEnum.ORDINARY_END.getCode());

        if (!StringUtils.isEmpty(userId)) {
            wrapper.eq("author_id", userId);
        }

        wrapper.orderByDesc("update_time");
        IPage<ArticleEntity> page = this.page(
                new Query<ArticleEntity>().getPage(params),
                wrapper
        );
        if (page.getTotal() == 0) {
            return new PageUtils(page);
        }
        Set<String> userIds = page.getRecords().stream().map(ArticleEntity::getAuthorId).collect(Collectors.toSet());
        Map<String, UserEntity> maps = userService.listByIds(userIds).stream().collect(Collectors.toMap(UserEntity::getUserId, u -> u));
        List<ArticleModel> articleModels = new ArrayList<>();
        page.getRecords().forEach((a) -> {
            ArticleModel articleModel = new ArticleModel();
            BeanUtils.copyProperties(a, articleModel);
            articleModel.setContent(null);
            // 数据脱敏
            UserEntity userEntity = maps.get(a.getAuthorId());
            userEntity.clean();
            articleModel.setUser(userEntity);
            articleModels.add(articleModel);
        });
        return new PageUtils(new CustomPage<>(articleModels, page.getTotal(), page.getSize(), page.getCurrent(), page.orders()));
    }


    @Override
    public ArticleModel getArticleInfo(Long id, HttpServletRequest request) {
        ArticleEntity articleEntity = this.getById(id);
        if (articleEntity == null) {
            return null;
        }
        if (ArticleTypeEnum.checkCourseArticle(articleEntity.getType())) {
            return null;
        }
        Claims user = null;
        try {
            user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        } catch (Exception ignored) { }

        if (articleEntity.getType() == ArticleTypeEnum.DRAFT.getCode()) {
            if (user != null) {
                if (!user.getId().equals(articleEntity.getAuthorId())) {
                    return null;
                }
            }
        }

        UserEntity userEntity = userService.getById(articleEntity.getAuthorId());
        userEntity.clean();
        ArticleModel articleModel = new ArticleModel();
        BeanUtils.copyProperties(articleEntity, articleModel);
        articleModel.setUser(userEntity);
        if (ArticleTypeEnum.isVote(articleEntity.getType())) {
            List<VoteMode> voteList = voteService.getVoteModeList(articleEntity.getId());
            articleModel.setVotes(voteList);
            // 检查当前投票状态
            if (user != null) {
                articleModel.setVoteLog(voteService.voteLogEntity(articleEntity.getId(), user.getId()));
            } else {
                articleModel.setVoteLog(null);
            }
        }
        try {
            articleModel.setTag((List<String>) objectMapper.readValue(articleEntity.getTag(), List.class));
        } catch (Exception ignored) {
        }
        // TODO 阅读量加 1
        return articleModel;
    }
}