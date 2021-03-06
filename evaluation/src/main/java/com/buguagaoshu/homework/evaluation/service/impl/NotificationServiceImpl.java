package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.ArticleTypeEnum;
import com.buguagaoshu.homework.common.enums.NotificationTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.*;
import com.buguagaoshu.homework.evaluation.service.CurriculumService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.NotificationDao;
import com.buguagaoshu.homework.evaluation.service.NotificationService;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pu Zhiwei
 */
@Service("notificationService")
public class NotificationServiceImpl extends ServiceImpl<NotificationDao, NotificationEntity> implements NotificationService {
    private CurriculumService curriculumService;

    @Autowired
    public void setCurriculumService(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        QueryWrapper<NotificationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver", user.getId());
        String type = (String) params.get("type");
        if (!StringUtils.isEmpty(type)) {
            switch (type) {
                case "0":
                    wrapper.ge("type", 0);
                    wrapper.le("type", 99);
                    break;
                case "1":
                    wrapper.ge("type", 100);
                    wrapper.le("type", 199);
                    break;
                case "2":
                    wrapper.ge("type", 200);
                    wrapper.le("type", 299);
                    break;
                default:
                    wrapper.ge("type", 300);
                    wrapper.le("type", 399);
                    break;
            }

        }
        String status = (String) params.get("status");

        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        IPage<NotificationEntity> page = this.page(
                new Query<NotificationEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void send(String notifier, String notifierName, String receiver, NotificationTypeEnum type, String text, String url, Long outerId) {
        long time = System.currentTimeMillis();
        NotificationEntity notificationEntity = initNotificationEntity(notifier, notifierName, receiver, time, type, outerId);
        notificationEntity.setText(text);
        notificationEntity.setUrl(url);
        this.save(notificationEntity);
    }

    @Override
    public void sendComment(String notifier, String notifierName, String receiver, ArticleEntity articleEntity, CommentEntity commentEntity, NotificationTypeEnum type) {
        if (notifier.equals(receiver)) {
            return;
        }
        long time = System.currentTimeMillis();
        // 此时 目标ID应该记录为帖子ID，应为通知ID已被记录
        NotificationEntity notificationEntity = initNotificationEntity(notifier, notifierName, receiver, time, type, articleEntity.getId());
        notificationEntity.setCommentId(commentEntity.getId());
        if (commentEntity.getContent().length() > 50) {
            notificationEntity.setCommentContent(commentEntity.getContent().substring(0, 50) + "......");
        } else {
            notificationEntity.setCommentContent(commentEntity.getContent());
        }
        if (articleEntity.getType() == ArticleTypeEnum.THINK.getCode()) {
            notificationEntity.setType(NotificationTypeEnum.THINK_COMMENT.getCode());
        }
        notificationEntity.setText(articleEntity.getTitle());
        notificationEntity.setUrl(null);
        this.save(notificationEntity);
    }

    @Override
    public void sendComment(String notifier, String notifierName,
                            String receiver, SubmitHomeworkStatusEntity userSubmit,
                            CommentEntity commentEntity, HomeworkEntity homeworkEntity) {
        if (notifier.equals(receiver)) {
            return;
        }
        long time = System.currentTimeMillis();
        NotificationEntity notificationEntity = initNotificationEntity(notifier, notifierName, receiver, time, NotificationTypeEnum.HOMEWORK_HAVE_NEW_COMMENT, userSubmit.getId());
        notificationEntity.setCommentId(commentEntity.getId());
        if (commentEntity.getContent().length() > 50) {
            notificationEntity.setCommentContent(commentEntity.getContent().substring(0, 50) + "......");
        } else {
            notificationEntity.setCommentContent(commentEntity.getContent());
        }
        notificationEntity.setText(homeworkEntity.getTitle());
        notificationEntity.setUrl("/course/learn/" + homeworkEntity.getClassNumber() + "/evaluation/homework/" + homeworkEntity.getId() + "/comment/" + userSubmit.getId());
        this.save(notificationEntity);
    }

    @Override
    public void sendBulletin(List<StudentsCurriculumEntity> userList, String notifier, String notifierName, Long courseId) {
        if (userList.size() != 0) {
            CurriculumEntity curriculumEntity = curriculumService.getById(courseId);
            long time = System.currentTimeMillis();
            List<NotificationEntity> notificationEntities = new LinkedList<>();
            String text = "你学习的课程：《" + curriculumEntity.getCurriculumName() + "》发布了新的公告！";
            String url = "/course/learn/" + courseId;
            userList.forEach((u) -> {
                NotificationEntity notificationEntity = initNotificationEntity(notifier, notifierName, u.getStudentId(), time, NotificationTypeEnum.COURSE_BULLETIN, courseId);
                // 消息通知显示内容
                notificationEntity.setText(text);
                notificationEntity.setUrl(url);

                notificationEntities.add(notificationEntity);
            });
            this.saveBatch(notificationEntities);
        }
    }

    @Override
    public void sendNewExam(List<StudentsCurriculumEntity> userListInCurriculum, String notifier, String notifierName, CurriculumEntity curriculumEntity, HomeworkEntity homeworkEntity) {
        if (userListInCurriculum.size() != 0) {
            long time = System.currentTimeMillis();
            List<NotificationEntity> notificationEntities = new LinkedList<>();
            String text = "你学习的课程：《" + curriculumEntity.getCurriculumName() + "》 发布了新的作业，" + homeworkEntity.getTitle() + "，请尽快查看！";
            String url = "/course/learn/" + curriculumEntity.getId() + "/exam";
            userListInCurriculum.forEach((u) -> {
                NotificationEntity notificationEntity = initNotificationEntity(notifier, notifierName, u.getStudentId(), time, NotificationTypeEnum.COURSE_EXAM, curriculumEntity.getId());
                notificationEntity.setText(text);
                notificationEntity.setUrl(url);
                notificationEntities.add(notificationEntity);
            });
            this.saveBatch(notificationEntities);
        }
    }

    @Override
    public void sendJoinCourseToUser(List<StudentsCurriculumEntity> students, String notifier, String notifierName, CurriculumEntity curriculumEntity) {
        if (students.size() != 0) {
            long time = System.currentTimeMillis();
            List<NotificationEntity> notificationEntities = new LinkedList<>();
            String text = "你已经被教师：" + notifier + " 导入到了课程《" + curriculumEntity.getCurriculumName() + "》，请及时进入学习";
            String url = "/course/learn/" + curriculumEntity.getId();
            students.forEach((u) -> {
                NotificationEntity notificationEntity = initNotificationEntity(notifier, notifierName, u.getStudentId(), time, NotificationTypeEnum.COURSE_JOIN, curriculumEntity.getId());
                notificationEntity.setText(text);
                notificationEntity.setUrl(url);
                notificationEntities.add(notificationEntity);
            });
            this.saveBatch(notificationEntities);
        }
    }

    @Override
    public boolean read(Long id, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        NotificationEntity notificationEntity = this.getById(id);
        if (notificationEntity == null) {
            return false;
        }
        if (notificationEntity.getReceiver().equals(user.getId())) {
            notificationEntity.setStatus(NotificationTypeEnum.READ.getCode());
            this.updateById(notificationEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean readAll(HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        List<NotificationEntity> receiver = list(
                new QueryWrapper<NotificationEntity>()
                        .eq("receiver", user.getId())
                        .eq("status", NotificationTypeEnum.UNREAD.getCode()));
        if (receiver.size() != 0) {
            receiver.forEach((e) -> {
                e.setStatus(NotificationTypeEnum.READ.getCode());
            });
            this.updateBatchById(receiver);
            return true;
        }
        return false;
    }

    @Override
    public void sendNewCourseware(List<StudentsCurriculumEntity> students, String userId, String username, CoursewareEntity coursewareEntity, String typeMessage) {
        if (students.size() != 0) {
            long time = System.currentTimeMillis();
            List<NotificationEntity> notificationEntities = new LinkedList<>();
            String text = "你学习的课程：发布了新的课件：" + coursewareEntity.getTitle() + "，请尽快查看！";
            String url = "/course/learn/" + coursewareEntity.getCourseId() + "/courseware/" + coursewareEntity.getId();
            students.forEach((u) -> {
                NotificationEntity notificationEntity = initNotificationEntity(userId, username, u.getStudentId(), time, NotificationTypeEnum.COURSE_COURSEWARE, coursewareEntity.getCourseId());
                notificationEntity.setText(text);
                notificationEntity.setUrl(url);
                notificationEntities.add(notificationEntity);
            });
            this.saveBatch(notificationEntities);
        }
    }


    /**
     * 初始化消息基础信息
     */
    private NotificationEntity initNotificationEntity(String notifier,
                                                      String notifierName,
                                                      String receiver,
                                                      long time,
                                                      NotificationTypeEnum type,
                                                      long outerId) {
        NotificationEntity notificationEntity = new NotificationEntity();
        // 设置发送人
        notificationEntity.setNotifier(notifier);
        notificationEntity.setNotifierName(notifierName);
        // 设置接收人
        notificationEntity.setReceiver(receiver);
        // 创建时间
        notificationEntity.setCreateTime(time);
        // 消息类型
        notificationEntity.setType(type.getCode());
        // 设置外部课程 ID
        notificationEntity.setOuterId(outerId);
        notificationEntity.setStatus(NotificationTypeEnum.UNREAD.getCode());
        return notificationEntity;
    }
}