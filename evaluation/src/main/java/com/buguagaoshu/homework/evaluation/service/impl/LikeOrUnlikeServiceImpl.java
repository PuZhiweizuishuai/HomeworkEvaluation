package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.entity.CommentEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.service.ArticleService;
import com.buguagaoshu.homework.evaluation.service.CommentService;
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

import com.buguagaoshu.homework.evaluation.dao.LikeOrUnlikeDao;
import com.buguagaoshu.homework.evaluation.entity.LikeOrUnlikeEntity;
import com.buguagaoshu.homework.evaluation.service.LikeOrUnlikeService;

import javax.servlet.http.HttpServletRequest;

/**
 * 数据库实现点赞
 * 后期加缓存
 * */
@Service("likeOrUnlikeService")
public class LikeOrUnlikeServiceImpl extends ServiceImpl<LikeOrUnlikeDao, LikeOrUnlikeEntity> implements LikeOrUnlikeService {

    private final ArticleService articleService;

    private final CommentService commentService;

    @Autowired
    public LikeOrUnlikeServiceImpl(ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LikeOrUnlikeEntity> page = this.page(
                new Query<LikeOrUnlikeEntity>().getPage(params),
                new QueryWrapper<LikeOrUnlikeEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean clickLick(LikeOrUnlikeEntity likeOrUnlikeEntity, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        long time = System.currentTimeMillis();
        likeOrUnlikeEntity.setCreateTime(time);
        likeOrUnlikeEntity.setStatus(0);
        likeOrUnlikeEntity.setReceiverUser(user.getId());
        likeOrUnlikeEntity.setUpdateTime(time);
        likeOrUnlikeEntity.setReceiverUserName(user.getSubject());
        QueryWrapper<LikeOrUnlikeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver_user", user.getId());
        wrapper.eq("target_id", likeOrUnlikeEntity.getTargetId());
        // 点赞帖子
        if (likeOrUnlikeEntity.getTargetType() == 0) {
            wrapper.eq("target_type", 0);
            ArticleEntity articleEntity = articleService.getById(likeOrUnlikeEntity.getTargetId());
            if (user.getId().equals(articleEntity.getAuthorId())) {
                throw new UserDataFormatException("不能给自己点赞！");
            }
            if (articleEntity == null) {
                throw new UserDataFormatException("点赞帖子不存在或已被删除！");
            }
            LikeOrUnlikeEntity one = this.getOne(wrapper);
            if (one != null) {
                this.removeById(one.getId());
                articleService.countNumberCount("like_count", articleEntity.getId(), -1);
                throw new UserDataFormatException("取消点赞成功！");
            } else {
                likeOrUnlikeEntity.setTargetUserId(articleEntity.getAuthorId());
                this.save(likeOrUnlikeEntity);
                articleService.countNumberCount("like_count", articleEntity.getId(), 1);
                return true;
            }
        } else {
            wrapper.eq("target_type", 1);
            CommentEntity commentEntity = commentService.getById(likeOrUnlikeEntity.getTargetId());
            if (user.getId().equals(commentEntity.getAuthorId())) {
                throw new UserDataFormatException("不能给自己点赞！");
            }
            if (commentEntity == null) {
                throw new UserDataFormatException("点赞评论不存在或已被删除！");
            }
            LikeOrUnlikeEntity one = this.getOne(wrapper);
            if (one != null) {
                this.removeById(one.getId());
                commentService.countAdd("like_count", commentEntity.getId(), -1);
                throw new UserDataFormatException("取消点赞成功！");
            } else {
                likeOrUnlikeEntity.setTargetUserId(commentEntity.getAuthorId());
                this.save(likeOrUnlikeEntity);
                commentService.countAdd("like_count", commentEntity.getId(), 1);
                return true;
            }
        }
    }

}