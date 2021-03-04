package com.buguagaoshu.homework.evaluation.service;

import com.buguagaoshu.homework.common.domain.AtUser;
import com.buguagaoshu.homework.common.enums.AtUserTypeEnum;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.entity.CommentEntity;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-04 22:28
 * 处理 @ 用户
 */
public interface AtUserService {
    /**
     * @param atUsers 被@用户列表
     * @param typeEnum 类型
     * @param article 文章
     * @param comment 评论
     * @param userId 发送@的用户
     * @param username 发送@的用户名
     * @return JSON 字符串
     * */
    String atUser(List<AtUser> atUsers, AtUserTypeEnum typeEnum, ArticleEntity article, CommentEntity comment, String userId, String username);
}
