package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.AtUser;
import com.buguagaoshu.homework.common.enums.AtUserTypeEnum;
import com.buguagaoshu.homework.common.enums.NotificationTypeEnum;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.entity.CommentEntity;
import com.buguagaoshu.homework.evaluation.entity.NotificationEntity;
import com.buguagaoshu.homework.evaluation.service.AtUserService;
import com.buguagaoshu.homework.evaluation.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-04 22:38
 */
@Service
@Slf4j
public class AtUserServiceImpl implements AtUserService {

    private NotificationService notificationService;

    private final ObjectMapper objectMapper;

    @Autowired
    public AtUserServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String atUser(List<AtUser> atUsers, AtUserTypeEnum typeEnum, ArticleEntity article, CommentEntity comment, String userId, String username) {

        if (atUsers != null && atUsers.size() != 0) {
            try {
                String atStr = objectMapper.writeValueAsString(atUsers);
                // TODO 目测此处不需要判断用户是否存在，直接发送通知就行, 要有BUG之后再改
                List<NotificationEntity> notificationEntities = new ArrayList<>(atUsers.size());
                for (AtUser at : atUsers) {
                    NotificationEntity notificationEntity = new NotificationEntity();

                    notificationEntity.setNotifier(userId);
                    notificationEntity.setNotifierName(username);

                    notificationEntity.setReceiver(at.getUserId());
                    notificationEntity.setOuterId(article.getId());
                    notificationEntity.setType(NotificationTypeEnum.AT_USER.getCode());
                    // 评论 @
                    String content = "";
                    if (typeEnum.equals(AtUserTypeEnum.COMMENT_AT)) {
                        notificationEntity.setCommentId(comment.getId());
                        content = comment.getContent();
                    } else {
                        content = article.getContent();
                    }
                    if (content.length() > 50) {
                        notificationEntity.setCommentContent(content.substring(0, 50) + "......");
                    } else {
                        notificationEntity.setCommentContent(content);
                    }
                    notificationEntity.setText(article.getTitle());

                    notificationEntities.add(notificationEntity);

                }
                notificationService.saveBatch(notificationEntities);
                return atStr;
            } catch (JsonProcessingException e) {
                log.error("AT 用户序列化失败！{}, AT 类型： {}, AT 发生目标帖子ID： {}, 操作用户ID： {}",
                        e.getMessage(),
                        typeEnum.getCode(),
                        article.getId(),
                        userId
                );
                return "";
            }
        } else {
            return "";
        }
    }
}
