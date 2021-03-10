package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.ConvertOfficeInfo;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.NotificationService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.common.utils.AesUtil;
import com.buguagaoshu.homework.common.utils.FileUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.CoursewareDao;
import com.buguagaoshu.homework.evaluation.entity.CoursewareEntity;
import com.buguagaoshu.homework.evaluation.service.CoursewareService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pu Zhiwei
 */
@Service("coursewareService")
public class CoursewareServiceImpl extends ServiceImpl<CoursewareDao, CoursewareEntity> implements CoursewareService {

    private final StudentsCurriculumService studentsCurriculumService;

    private final NotificationService notificationService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private final WebConstant webConstant;
    private final FileUtil fileUtil;

    @Autowired
    public CoursewareServiceImpl(StudentsCurriculumService studentsCurriculumService, NotificationService notificationService, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, WebConstant webConstant, FileUtil fileUtil) {
        this.studentsCurriculumService = studentsCurriculumService;
        this.notificationService = notificationService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.webConstant = webConstant;
        this.fileUtil = fileUtil;
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CoursewareEntity> page = this.page(
                new Query<CoursewareEntity>().getPage(params),
                new QueryWrapper<CoursewareEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CoursewareEntity> coursewareTree(Long courseId, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        StudentsCurriculumEntity student =
                studentsCurriculumService.selectStudentByCurriculumId(user.getId(), courseId);
        if (student == null) {
            return null;
        }
        QueryWrapper<CoursewareEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        List<CoursewareEntity> list = this.list(wrapper);
        list.forEach(e -> {
            if (!StringUtils.isEmpty(e.getFileUrl())) {
                String name = e.getFileUrl().substring(e.getFileUrl().lastIndexOf("/") + 1);
                String strToEncrypt = user.getId() + "#" + name + "#" + (System.currentTimeMillis() + WebConstant.AES_EXPIRES_TIME);
                String key = AesUtil.encrypt(strToEncrypt, webConstant.getAesKey());
                e.setKey(key);
            }
        });

        // TODO 观看历史记录
        List<CoursewareEntity> levelTree =
                list.stream().filter((coursewareEntity -> coursewareEntity.getLevel() == 0))
                        .peek((courseware) -> courseware.setChildren(getChildren(courseware, list)))
                        .sorted(Comparator.comparingInt(sort -> (sort.getSort() == null ? 1 : sort.getSort())))
                        .collect(Collectors.toList());
        return levelTree;
    }

    @Override
    @Transactional(rollbackFor = {})
    public boolean saveCourseware(CoursewareEntity coursewareEntity, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        StudentsCurriculumEntity student =
                studentsCurriculumService.selectStudentByCurriculumId(user.getId(), coursewareEntity.getCourseId());
        if (student == null) {
            return false;
        }
        if (student.getRole().equals(RoleTypeEnum.TEACHER.getRole())) {
            long time = System.currentTimeMillis();
            if (coursewareEntity.getFatherId() != null && coursewareEntity.getFatherId() != 0) {
                CoursewareEntity byId = this.getById(coursewareEntity.getFatherId());
                if (byId == null) {
                    return false;
                }
                if (!byId.getCourseId().equals(coursewareEntity.getCourseId())) {
                    return false;
                }
            }

            coursewareEntity.setCreateTeacher(user.getId());
            coursewareEntity.setCaretTime(time);
            coursewareEntity.setUpdateTime(time);
            if (coursewareEntity.getLevel() == 0) {
                coursewareEntity.setFatherId(0L);
            }

            // 设置文件格式
            if (!StringUtils.isEmpty(coursewareEntity.getFileUrl())) {
                coursewareEntity.setFileType(fileUtil.fileTypeCode(coursewareEntity.getFileUrl()));
                if (coursewareEntity.getFileType() == FileUtil.OFFICE_CODE) {
                    // 转码中
                    coursewareEntity.setStatus(1);
                } else {
                    coursewareEntity.setStatus(0);
                }
            } else {
                coursewareEntity.setStatus(0);
            }

            this.save(coursewareEntity);
            // 发送转码消息
            if (coursewareEntity.getFileType() != null && coursewareEntity.getFileType() == FileUtil.OFFICE_CODE) {
                ConvertOfficeInfo convertOfficeInfo = new ConvertOfficeInfo();
                convertOfficeInfo.setFilename(fileUtil.getNewFileName(coursewareEntity.getFileUrl()));
                convertOfficeInfo.setFilePath(fileUtil.removeApiWithName(coursewareEntity.getFileUrl()));
                convertOfficeInfo.setTargetFilePath("");
                convertOfficeInfo.setUsername(user.getSubject());
                convertOfficeInfo.setUserID(user.getId());
                convertOfficeInfo.setCoursewareId(coursewareEntity.getId());
                try {
                    kafkaTemplate.send("ConvertOffice", objectMapper.writeValueAsString(convertOfficeInfo));
                } catch (Exception ignored) {}
            } else {
                // 发送通知
                List<StudentsCurriculumEntity> students = studentsCurriculumService.findUserListInCurriculum(coursewareEntity.getCourseId());
                notificationService.sendNewCourseware(students, user.getId(), user.getSubject(), coursewareEntity);
            }
            return true;
        }
        return false;
    }


    private List<CoursewareEntity> getChildren(CoursewareEntity root,
                                               List<CoursewareEntity> all) {
        List<CoursewareEntity> children = all.stream().filter((categoryEntity) -> categoryEntity.getFatherId().equals(root.getId()))
                .peek((courseTagEntity) -> {
                    // 查找子分类
                    courseTagEntity.setChildren(getChildren(courseTagEntity, all));
                })
                .sorted(Comparator.comparingInt(tag -> (tag.getSort() == null ? 0 : tag.getSort())))
                .collect(Collectors.toList());
        return children;
    }

}