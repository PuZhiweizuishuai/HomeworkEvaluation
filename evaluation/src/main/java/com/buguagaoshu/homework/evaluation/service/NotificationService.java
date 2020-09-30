package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.enums.NotificationTypeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 通知表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-26 21:36:59
 */
public interface NotificationService extends IService<NotificationEntity> {

    /**
     * 获取消息通知列表
     *
     * @param params  查找参数
     *                page: 页码
     *                limit： 每页显示数量
     *                type： 类型
     *                0 课程通知
     *                1 社区通知
     *                2 私信
     *                3 系统消息
     *                status： 是否已读
     *                0 未读
     *                1 已读
     * @param request 请求用户
     * @return 消息列表
     */
    PageUtils queryPage(Map<String, Object> params, HttpServletRequest request);

    /**
     * 发送消息
     *
     * @param notifier     消息发送者
     * @param notifierName 消息发送人昵称
     * @param receiver     消息接收者
     * @param type         消息类型
     * @param text         消息描述
     * @param url          跳转链接
     * @param outerId      外部ID
     * @return 发送结果
     */
    void send(String notifier, String notifierName, String receiver, NotificationTypeEnum type, String text, String url, Long outerId);


    /**
     * 发送新评论通知
     *
     * @param notifier 消息发送者
     * @param receiver 消息接收者
     */
    void sendComment(String notifier, String notifierName, String receiver, ArticleEntity articleEntity, CommentEntity commentEntity, NotificationTypeEnum type);


    /**
     * 发送新课程公告的通知
     *
     * @param userList 消息接收人列表
     * @param notifier 消息发送人
     * @param courseId 课程ID
     */
    void sendBulletin(List<StudentsCurriculumEntity> userList, String notifier, String notifierName, Long courseId);

    /**
     * 发送新作业公告
     *
     * @param userListInCurriculum 消息接收人列表
     * @param notifier             消息发送人
     * @param curriculumEntity     课程信息
     * @param homeworkEntity       作业信息
     */
    void sendNewExam(List<StudentsCurriculumEntity> userListInCurriculum, String notifier, String notifierName, CurriculumEntity curriculumEntity, HomeworkEntity homeworkEntity);


    /**
     * 给学生发送已经被导入课程的通知
     *
     * @param students         消息接收人列表
     * @param curriculumEntity 课程信息
     * @param notifier         消息发送人
     */
    void sendJoinCourseToUser(List<StudentsCurriculumEntity> students,  String notifier, String notifierName, CurriculumEntity curriculumEntity);

    /**
     * 将通知设置为已读
     *
     * @param id      通知ID
     * @param request 获取请求用户
     * @return 结果
     */
    boolean read(Long id, HttpServletRequest request);


    /**
     * 一键已读
     *
     * @param request 请求用户
     * @return 结果
     */
    boolean readAll(HttpServletRequest request);
}

