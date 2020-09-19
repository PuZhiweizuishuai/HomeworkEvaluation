package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.StudentsCurriculumEntity;
import com.buguagaoshu.homework.evaluation.service.StudentsCurriculumService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Stack;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-19 19:48
 */
@RestController
public class StudentWithCurriculumController {
    private final StudentsCurriculumService studentsCurriculumService;

    @Autowired
    public StudentWithCurriculumController(StudentsCurriculumService studentsCurriculumService) {
        this.studentsCurriculumService = studentsCurriculumService;
    }

    @GetMapping("/curriculum/role/{id}")
    public ResponseDetails userRoleInCourse(@PathVariable("id") Long courseId,
                                            HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        StudentsCurriculumEntity student = studentsCurriculumService.selectStudentByCurriculumId(user.getId(), courseId);
        if (student == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NOO_FOUND);
        }
        return ResponseDetails.ok().put("data", student);
    }
}
