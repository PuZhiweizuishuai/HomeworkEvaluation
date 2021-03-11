package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.ConvertOfficeInfo;
import com.buguagaoshu.homework.common.domain.FileModel;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.feign.OssFeignService;
import com.buguagaoshu.homework.evaluation.service.NotificationService;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.common.utils.AesUtil;
import com.buguagaoshu.homework.common.utils.FileUtil;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserService userService;

    private final OssFeignService ossFeignService;

    private final ObjectMapper objectMapper;

    private final WebConstant webConstant;
    private final FileUtil fileUtil;

    @Autowired
    public CoursewareServiceImpl(StudentsCurriculumService studentsCurriculumService, NotificationService notificationService, KafkaTemplate<String, String> kafkaTemplate, BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService, OssFeignService ossFeignService, ObjectMapper objectMapper, WebConstant webConstant, FileUtil fileUtil) {
        this.studentsCurriculumService = studentsCurriculumService;
        this.notificationService = notificationService;
        this.kafkaTemplate = kafkaTemplate;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.ossFeignService = ossFeignService;
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
            sendConvertMessage(coursewareEntity, user, "你学习的课程：发布了新的课件：");
            return true;
        }
        return false;
    }

    public void sendConvertMessage(CoursewareEntity coursewareEntity, Claims user, String typeMsg) {
        // 发送转码消息
        if (coursewareEntity.getFileType() != null && coursewareEntity.getFileType() == FileUtil.OFFICE_CODE) {
            ConvertOfficeInfo convertOfficeInfo = new ConvertOfficeInfo();
            convertOfficeInfo.setFilename(fileUtil.getNewFileName(coursewareEntity.getFileUrl()));
            convertOfficeInfo.setFilePath(fileUtil.removeApiWithName(coursewareEntity.getFileUrl()));
            convertOfficeInfo.setTargetFilePath("");
            convertOfficeInfo.setUsername(user.getSubject());
            convertOfficeInfo.setUserID(user.getId());
            convertOfficeInfo.setCoursewareId(coursewareEntity.getId());
            convertOfficeInfo.setTypeMsg(typeMsg);
            try {
                kafkaTemplate.send("ConvertOffice", objectMapper.writeValueAsString(convertOfficeInfo));
            } catch (Exception ignored) {}
        } else {
            // 发送通知
            List<StudentsCurriculumEntity> students = studentsCurriculumService.findUserListInCurriculum(coursewareEntity.getCourseId());
            notificationService.sendNewCourseware(students, user.getId(), user.getSubject(), coursewareEntity, typeMsg);
        }
    }

    @Override
    public boolean updateCourseware(CoursewareEntity coursewareEntity, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        CoursewareEntity entity = this.getById(coursewareEntity.getId());
        if (entity.getCreateTeacher().equals(user.getId())) {
            // 检查文件选项
            if (!StringUtils.isEmpty(coursewareEntity.getFileUrl())) {
                // 文件改变
                if (!coursewareEntity.getFileUrl().equals(entity.getFileUrl())) {
                    // 删除旧文件
                    sendDeleteFile(entity, request);
                    coursewareEntity.setFileType(fileUtil.fileTypeCode(coursewareEntity.getFileUrl()));
                    if (coursewareEntity.getFileType() == FileUtil.OFFICE_CODE) {
                        // 转码中
                        coursewareEntity.setStatus(1);
                    } else {
                        coursewareEntity.setStatus(0);
                    }
                }
            }
            // 发送消息
            sendConvertMessage(coursewareEntity, user, "原课件：《" + entity.getTitle() + "》发生了修改：");
            entity.update(coursewareEntity);
            this.updateById(entity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delete(CoursewareEntity coursewareEntity, HttpServletRequest request) {
        // 验证密码
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        UserEntity userEntity = userService.getById(user.getId());
        if (!bCryptPasswordEncoder.matches(coursewareEntity.getPassword(), userEntity.getPassword())) {
            throw new UserDataFormatException("密码错误！");
        }
        CoursewareEntity id = this.getById(coursewareEntity.getId());
        List<Long> deleteIds = new ArrayList<>();
        deleteIds.add(id.getId());
        if (id.getCreateTeacher().equals(user.getId())) {
            // 查找子目录
            if (id.getFatherId() == 0) {
                QueryWrapper<CoursewareEntity> wrapper = new QueryWrapper<>();
                wrapper.eq("father_id", id.getId());
                List<CoursewareEntity> list = this.list(wrapper);
                list.forEach((l) -> {
                    sendDeleteFile(l, request);
                    deleteIds.add(l.getId());
                });
            }
            // 发送文件删除消息
            sendDeleteFile(coursewareEntity, request);
            this.baseMapper.deleteBatchIds(deleteIds);
            return true;
        } else {
            return false;
        }

    }

    public void sendDeleteFile(CoursewareEntity coursewareEntity, HttpServletRequest request) {
        if (StringUtils.isEmpty(coursewareEntity.getFileUrl())) {
            return;
        }
        FileModel fileModel = new FileModel();
        String path = coursewareEntity.getFileUrl().substring(4);
        fileModel.setPath(path);
        String token = JwtUtil.getToken(request);
        fileModel.setToken(token);
        // 删除旧文件
        ossFeignService.deleteCoursewareFile(fileModel);
        // 如果旧文件是OFFICE文档，删除转码后的文件
        if (coursewareEntity.getFileType() == FileUtil.OFFICE_CODE) {
            fileModel.setPath(path + ".pdf");
            ossFeignService.deleteCoursewareFile(fileModel);
        }
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