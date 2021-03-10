package com.buguagaoshu.homework.evaluation.listener;

import com.buguagaoshu.homework.evaluation.entity.CoursewareEntity;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.CoursewareService;
import com.buguagaoshu.homework.evaluation.service.NotificationService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 20:59
 * 监听转码成功的消息
 */
@Component
@Slf4j
public class ConvertMessageListener {
    private final StudentsCurriculumService studentsCurriculumService;

    private final NotificationService notificationService;

    private final CoursewareService coursewareService;

    public ConvertMessageListener(StudentsCurriculumService studentsCurriculumService, NotificationService notificationService, CoursewareService coursewareService) {
        this.studentsCurriculumService = studentsCurriculumService;
        this.notificationService = notificationService;
        this.coursewareService = coursewareService;
    }


    @KafkaListener(topics = {"ConvertMessage"})
    public void receiver(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            String message = (String) kafkaMessage.get();
            String[] str = message.split("#");
            // 转码成功
            CoursewareEntity coursewareEntity = coursewareService.getById(Long.parseLong(str[0]));
            System.out.println();
            System.out.println(message);
            System.out.println();
            if (str[1].equals("T")) {
                // 发送成功消息
                List<StudentsCurriculumEntity> students = studentsCurriculumService.findUserListInCurriculum(coursewareEntity.getCourseId());
                // 2 是ID， 3 是名称
                notificationService.sendNewCourseware(students, str[2], str[3], coursewareEntity);
                coursewareEntity.setStatus(0);
            } else {
                // 保存失败消息
                coursewareEntity.setStatus(2);
            }
            coursewareService.updateById(coursewareEntity);
        }
    }
}
