package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.service.VerifyCodeService;
import com.buguagaoshu.homework.evaluation.vo.ForgetPasswordVo;
import com.buguagaoshu.homework.evaluation.vo.RegisterUserVo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2019-11-27 19:57
 */
@RestController
public class VerifyCodeController {
    private final VerifyCodeService verifyCodeService;

    private final UserService userService;

    private static final String IMAGE_FORMAT = "png";


    public VerifyCodeController(VerifyCodeService verifyCodeService, UserService userService) {
        this.verifyCodeService = verifyCodeService;
        this.userService = userService;
    }

    private static InputStreamResource imageToInputStreamResource(Image image, String format) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, format, byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return new InputStreamResource(byteArrayInputStream);
    }

    @PostMapping("/verify/register/email")
    public ResponseDetails registerEmail(@RequestBody RegisterUserVo registerUserVo,
                                         HttpSession session) {
        // 检查验证码
        verifyCodeService.verify(WebConstant.VERIFY_CODE_KEY, registerUserVo.getVerifyCode(), session);
        // 发送验证码
        UserEntity userEntity = userService.findByEmail(registerUserVo.getEmail());
        if (userEntity != null) {
            return ResponseDetails.ok(ReturnCodeEnum.EMAIL_ALREADY_EXISTS);
        }
        verifyCodeService.send(registerUserVo.getEmail(), null, registerUserVo.getEmail(), session);
        return ResponseDetails.ok();
    }

    @PostMapping("/verify/email")
    public ResponseDetails sendEmail(@RequestBody ForgetPasswordVo forgetPasswordVo,
                                     HttpSession session) {
        // 检查验证码
        verifyCodeService.verify(WebConstant.VERIFY_CODE_KEY, forgetPasswordVo.getVerifyCode(), session);
        // 发送验证码
        UserEntity userEntity = userService.findByEmail(forgetPasswordVo.getEmail());
        if (userEntity == null) {
            return ResponseDetails.ok(404, "邮箱错误或者该邮箱暂未绑定账号，请联系管理员。");
        }
        verifyCodeService.send(forgetPasswordVo.getEmail(), userEntity, forgetPasswordVo.getEmail(), session);
        return ResponseDetails.ok();
    }


    @GetMapping("/verifyImage")
    public ResponseEntity<?> image(HttpSession session) throws IOException {
        Image image = verifyCodeService.image(session);
        InputStreamResource inputStreamResource = imageToInputStreamResource(image, IMAGE_FORMAT);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Pragma", "No-cache");
        httpHeaders.set("Cache-Control", "no-cache");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .contentType(MediaType.IMAGE_PNG)
                .body(inputStreamResource);
    }
}
