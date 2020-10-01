package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.CustomPage;
import com.buguagaoshu.homework.common.enums.ArticleTypeEnum;
import com.buguagaoshu.homework.common.enums.CommentTypeEnum;
import com.buguagaoshu.homework.common.enums.NotificationTypeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.model.CommentModel;
import com.buguagaoshu.homework.evaluation.model.ReplyComment;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.CommentVo;
import com.buguagaoshu.homework.evaluation.vo.DeleteVo;
import io.jsonwebtoken.Claims;
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

import com.buguagaoshu.homework.evaluation.dao.CommentDao;
import com.buguagaoshu.homework.evaluation.entity.CommentEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Pu Zhiwei
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {

    private final ArticleService articleService;

    private final StudentsCurriculumService studentsCurriculumService;

    private final UserService userService;

    private final VerifyCodeService verifyCodeService;

    private final NotificationService notificationService;

    @Autowired
    public CommentServiceImpl(ArticleService articleService, StudentsCurriculumService studentsCurriculumService, UserService userService, VerifyCodeService verifyCodeService, NotificationService notificationService) {
        this.articleService = articleService;
        this.studentsCurriculumService = studentsCurriculumService;
        this.userService = userService;
        this.verifyCodeService = verifyCodeService;
        this.notificationService = notificationService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommentEntity> page = this.page(
                new Query<CommentEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = {})
    public CommentModel saveComment(CommentVo commentVo, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String verifyCodeKey = (String) session.getAttribute("verifyCodeKey");
        verifyCodeService.verify(verifyCodeKey, commentVo.getVerifyCode());
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        ArticleEntity article = articleService.getById(commentVo.getArticleId());
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.initComment();
        CommentModel commentModel = new CommentModel();
        NotificationTypeEnum type = null;
        CommentEntity father = null;
        // 如果帖子状态是正常的
        if (article != null && article.getStatus() == ArticleTypeEnum.NORMAL.getCode()) {
            // 如果是课程内的帖子，学生必须在课程内
            if (article.getType() == ArticleTypeEnum.COURSE.getCode()) {
                StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), article.getCourseId());
                if (studentsCurriculumEntity == null) {
                    return null;
                }
                type = NotificationTypeEnum.COURSE_BBS_COMMENT;
                commentModel.setIsTeacher(studentsCurriculumEntity.getRole().equals(RoleTypeEnum.TEACHER.getRole()));
                // 非课程内帖子
            } else {
                type = NotificationTypeEnum.BBS_COMMENT;
            }
            // 二级评论
            if (commentVo.getCommentId() != null && commentVo.getType() == CommentTypeEnum.ORDINARY_SECOND_COMMENT.getCode()) {
                father = getById(commentVo.getCommentId());
                if (father != null && father.getStatus() == 0 && father.getArticleId().equals(commentVo.getArticleId())) {
                    BeanUtils.copyProperties(commentVo, commentEntity);
                } else {
                    // 没有权限
                    return null;
                }
                // 判断父级ID
                // 如果评论的对象父级ID是文章ID，那么新评论的父级ID就应该是评论的评论ID
                if (father.getType() == 0) {
                    commentEntity.setFatherId(father.getId());
                } else {
                    commentEntity.setFatherId(father.getFatherId());
                    this.baseMapper.countAdd("comment_count", father.getId(), 1);
                }
                this.baseMapper.countAdd("comment_count", commentEntity.getFatherId(), 1);
                // 一级评论
            } else {
                BeanUtils.copyProperties(commentVo, commentEntity);
                commentEntity.setFatherId(article.getId());
                commentEntity.setType(0);
                commentEntity.setCommentId(null);
            }
            commentEntity.setAuthorId(user.getId());
            commentEntity.setIp(IpUtil.getIpAddr(request));
            commentEntity.setUa(request.getHeader("user-agent"));
            this.save(commentEntity);

            BeanUtils.copyProperties(commentEntity, commentModel);
            UserEntity userEntity = userService.getById(user.getId());
            commentModel.setAuthorId(userEntity.getUserId());
            commentModel.setUsername(userEntity.getUsername());
            commentModel.setAvatarUrl(userEntity.getUserAvatarUrl());
            // TODO 将这里加1改到缓存中
            articleService.countNumberCount("comment_count", commentEntity.getArticleId(), 1);
            article.setLatestCommentTime(System.currentTimeMillis());
            article.setLatestCommentName(user.getId());
            articleService.updateById(article);
            // TODO 给关注帖子的人也发送通知
            // TODO 有重复通知的bug
            notificationService.sendComment(user.getId(), user.getSubject(), article.getAuthorId(), article, commentEntity, type);
            if (father != null && !article.getAuthorId().equals(father.getAuthorId())) {
                notificationService.sendComment(user.getId(), user.getSubject(), father.getAuthorId(), article, commentEntity, type);
            }
            return commentModel;
        } else {
            return null;
        }
    }

    @Override
    public PageUtils commentList(Long articleId, Map<String, Object> params, HttpServletRequest request) {
        QueryWrapper<CommentEntity> wrapper = new QueryWrapper<>();
        ArticleEntity articleEntity = articleService.getById(articleId);
        Claims user = null;
        Map<String, StudentsCurriculumEntity> teacherMap = null;
        if (articleEntity == null) {
            return null;
        }
        if (articleEntity.getStatus().equals(ArticleTypeEnum.DELETE.getCode())) {
            return null;
        }
        if (articleEntity.getType().equals(ArticleTypeEnum.COURSE.getCode())) {
            try {
                user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
            } catch (Exception e) {
                return null;
            }
            StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), articleEntity.getCourseId());
            if (studentsCurriculumEntity == null) {
                return null;
            }
            teacherMap = studentsCurriculumService.teacherList(articleEntity.getCourseId()).stream().collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, t -> t));
        }
        wrapper.eq("article_id", articleId);
        wrapper.eq("status", 0);
        wrapper.eq("type", CommentTypeEnum.ORDINARY_ONE_LEVEL_COMMENT.getCode());
        String sort = (String) params.get("sort");
        if (!StringUtils.isEmpty(sort)) {
            if ("1".equals(sort)) {
                wrapper.orderByDesc("create_time");
            } else if ("3".equals(sort)) {
                wrapper.orderByDesc("comment_count");
            }
        }
        IPage<CommentEntity> page = this.page(
                new Query<CommentEntity>().getPage(params),
                wrapper
        );
        if (page.getTotal() == 0) {
            return new PageUtils(page);
        }
        Set<String> userIds = page.getRecords().stream().map(CommentEntity::getAuthorId).collect(Collectors.toSet());
        Map<String, UserEntity> userEntityMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(UserEntity::getUserId, u -> u));
        Map<String, StudentsCurriculumEntity> finalTeacherMap = teacherMap;
        return pageUtils(page, userEntityMap, finalTeacherMap, null);
    }

    @Override
    public ReplyComment replyComment(Long commentId, HttpServletRequest request) {
        CommentEntity id = getById(commentId);
        if (id == null) {
            return null;
        }
        if (id.getStatus() == 1) {
            return null;
        }
        // 获取评论的帖子信息
        ArticleEntity articleEntity = articleService.getById(id.getArticleId());
        if (articleEntity == null) {
            return null;
        }
        if (articleEntity.getStatus().equals(ArticleTypeEnum.DELETE.getCode())) {
            return null;
        }
        articleEntity.setContent(null);
        CommentModel commentModel = new CommentModel();
        ReplyComment replyComment = new ReplyComment();
        replyComment.setArticleEntity(articleEntity);
        BeanUtils.copyProperties(id, commentModel);
        UserEntity u = userService.getById(id.getAuthorId());
        commentModel.setAvatarUrl(u.getUserAvatarUrl());
        commentModel.setUsername(u.getUsername());
        replyComment.setCommentModel(commentModel);
        return replyComment;
    }

    @Override
    public PageUtils secondCommentList(Long id, Map<String, Object> params, HttpServletRequest request) {
        CommentEntity commentEntity = getById(id);
        if (commentEntity == null) {
            return null;
        }
        // 评论被删除
        if (commentEntity.getStatus() == 1) {
            return null;
        }
        ArticleEntity articleEntity = articleService.getById(commentEntity.getArticleId());
        if (articleEntity.getType().equals(ArticleTypeEnum.COURSE.getCode())) {
            try {
                Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
                if (studentsCurriculumService.selectStudentByCurriculumId(user.getId(), articleEntity.getCourseId()) == null) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        QueryWrapper<CommentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("father_id", id);
        wrapper.eq("status", 0);
        wrapper.eq("type", CommentTypeEnum.ORDINARY_SECOND_COMMENT.getCode());
        IPage<CommentEntity> page = this.page(
                new Query<CommentEntity>().getPage(params),
                wrapper
        );
        if (page.getTotal() == 0) {
            return new PageUtils(page);
        }
        Set<String> userIds = page.getRecords().stream().map(CommentEntity::getAuthorId).collect(Collectors.toSet());
        Map<String, UserEntity> userEntityMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(UserEntity::getUserId, u -> u));
        Map<Long, CommentEntity> commentEntityMap = page.getRecords().stream().collect(Collectors.toMap(CommentEntity::getId, c -> c));
        return pageUtils(page, userEntityMap, null, commentEntityMap);
    }

    @Override
    public boolean deleteComment(DeleteVo deleteVo, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        CommentEntity commentEntity = getById(deleteVo.getId());
        if (commentEntity != null) {
            if (user.getId().equals(commentEntity.getAuthorId())) {
                commentEntity.setStatus(1);
                this.updateById(commentEntity);
                return true;
            }
            if (user.get("authorities").equals(RoleTypeEnum.ADMIN.getRole())) {
                commentEntity.setStatus(1);
                this.updateById(commentEntity);
                return true;
            }
            ArticleEntity service = articleService.getById(commentEntity.getAuthorId());
            if (service.getType().equals(ArticleTypeEnum.COURSE.getCode())) {
                List<StudentsCurriculumEntity> studentsCurriculumEntities = studentsCurriculumService.teacherList(service.getCourseId());
                for (StudentsCurriculumEntity s: studentsCurriculumEntities) {
                    if (s.getStudentId().equals(user.getId())) {
                        commentEntity.setStatus(1);
                        this.updateById(commentEntity);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public PageUtils pageUtils(IPage<CommentEntity> page, Map<String, UserEntity> userEntityMap, Map<String, StudentsCurriculumEntity> teacherMap, Map<Long, CommentEntity> commentEntityMap) {
        List<CommentModel> commentModels = new ArrayList<>(Integer.parseInt(String.valueOf(page.getSize())));
        page.getRecords().forEach((p) -> {
            CommentModel commentModel = new CommentModel();
            BeanUtils.copyProperties(p, commentModel);
            UserEntity u = userEntityMap.get(p.getAuthorId());
            commentModel.setAvatarUrl(u.getUserAvatarUrl());
            commentModel.setUsername(u.getUsername());
            if (commentEntityMap != null) {
                if (!p.getFatherId().equals(p.getCommentId())) {
                    CommentEntity commentEntity = commentEntityMap.get(p.getCommentId());
                    UserEntity r = userEntityMap.get(commentEntity.getAuthorId());
                    commentModel.setReplyUserAvatar(r.getUserAvatarUrl());
                    commentModel.setReplyUserName(r.getUsername());
                    commentModel.setReplyUserId(r.getUserId());
                }
            }
            if (teacherMap != null) {
                commentModel.setIsTeacher(teacherMap.get(p.getAuthorId()) != null);
            }
            commentModels.add(commentModel);

        });
        return new PageUtils(new CustomPage<>(commentModels, page.getTotal(), page.getSize(), page.getCurrent(), page.orders()));
    }
}