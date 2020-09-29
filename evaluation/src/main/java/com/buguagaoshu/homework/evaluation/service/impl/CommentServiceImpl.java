package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.CustomPage;
import com.buguagaoshu.homework.common.enums.ArticleTypeEnum;
import com.buguagaoshu.homework.common.enums.NotificationTypeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.model.CommentModel;
import com.buguagaoshu.homework.evaluation.service.*;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.CommentVo;
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
 * */
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
                new QueryWrapper<CommentEntity>()
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
        // 如果帖子状态是正常的
        if (article != null && article.getStatus() == ArticleTypeEnum.NORMAL.getCode()) {
            // 如果是课程内的帖子，学生必须在课程内
            if (article.getType() == ArticleTypeEnum.COURSE.getCode()) {
                StudentsCurriculumEntity studentsCurriculumEntity = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), article.getCourseId());
                if (studentsCurriculumEntity == null) {
                    return null;
                }
                // 二级评论，回复评论
                if (commentVo.getCommentId() != null && commentVo.getType() == 1) {
                    // 查找父评论
                    CommentEntity father = getById(commentVo.getCommentId());
                    if (father != null && father.getStatus() == 0 && father.getArticleId().equals(commentVo.getArticleId())) {
                        BeanUtils.copyProperties(commentVo, commentEntity);
                    } else {
                        // 没有权限
                        return null;
                    }
                    // 一级评论
                } else {
                    BeanUtils.copyProperties(commentVo, commentEntity);
                    commentEntity.setType(0);
                    commentEntity.setCommentId(null);
                }
                type = NotificationTypeEnum.COURSE_BBS_COMMENT;
                commentModel.setIsTeacher(studentsCurriculumEntity.getRole().equals(RoleTypeEnum.TEACHER.getRole()));
                // 非课程内帖子
            } else {
                if (commentVo.getCommentId() != null && commentVo.getType() == 1) {
                    CommentEntity father = getById(commentVo.getCommentId());
                    if (father != null && father.getStatus() == 0 && father.getArticleId().equals(commentVo.getArticleId())) {
                        BeanUtils.copyProperties(commentVo, commentEntity);
                    } else {
                        // 没有权限
                        return null;
                    }
                } else {
                    BeanUtils.copyProperties(commentVo, commentEntity);
                    commentEntity.setType(0);
                    commentEntity.setCommentId(null);
                }
                type = NotificationTypeEnum.BBS_COMMENT;
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
            // TODO 给关注帖子的人也发送通知
            notificationService.sendComment(user.getId(), article.getAuthorId(), article, commentEntity, type);
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
            teacherMap = studentsCurriculumService.teacherList(articleEntity.getCourseId()).stream().collect(Collectors.toMap(StudentsCurriculumEntity::getStudentId, t->t));
        }
        wrapper.eq("article_id", articleId);
        wrapper.eq("status", 0);
        String sort = (String) params.get("sort");
        if (!StringUtils.isEmpty(sort)) {
            if (sort.equals("0")) {
                wrapper.orderByDesc("create_time");
            }
        } else {
            wrapper.orderByDesc("create_time");
        }
        IPage<CommentEntity> page = this.page(
                new Query<CommentEntity>().getPage(params),
                wrapper
        );
        Set<String> userIds =  page.getRecords().stream().map(CommentEntity::getAuthorId).collect(Collectors.toSet());
        Map<String, UserEntity> userEntityMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(UserEntity::getUserId, u->u));
        List<CommentModel> commentModels = new ArrayList<>(Integer.parseInt(String.valueOf(page.getSize())));
        Map<String, StudentsCurriculumEntity> finalTeacherMap = teacherMap;
        page.getRecords().forEach((p) -> {
            CommentModel commentModel = new CommentModel();
            BeanUtils.copyProperties(p, commentModel);
            UserEntity u = userEntityMap.get(p.getAuthorId());
            commentModel.setAvatarUrl(u.getUserAvatarUrl());
            commentModel.setUsername(u.getUsername());
            if (finalTeacherMap != null) {
                commentModel.setIsTeacher(finalTeacherMap.get(p.getAuthorId()) != null);
            }
            commentModels.add(commentModel);

        });
        return new PageUtils(new CustomPage<>(commentModels, page.getTotal(), page.getSize(),page.getCurrent(),page.orders()));
    }

}