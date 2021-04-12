package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.service.VerifyCodeService;
import com.buguagaoshu.homework.evaluation.vo.ForgetPasswordVo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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

    @PostMapping("/verify/email")
    public ResponseDetails sendEmail(@RequestBody ForgetPasswordVo forgetPasswordVo,
                                     HttpSession session) {
        // 检查验证码
        verifyCodeService.verify(session.getId(), forgetPasswordVo.getVerifyCode());
        // 发送验证码
        UserEntity userEntity = userService.findByEmail(forgetPasswordVo.getEmail());
        if (userEntity == null) {
            return ResponseDetails.ok(404, "邮箱错误或者该邮箱暂未绑定账号，请联系管理员。");
        }
        verifyCodeService.send(forgetPasswordVo.getEmail(), userEntity);
        return ResponseDetails.ok();
    }


    @GetMapping("/verifyImage")
    public HttpEntity image(HttpSession session) throws IOException {
        // TODO 此处是为了解决 Session ID 一致性的问题，在采用 Spring Session Redis后，保存的验证码使用的 Session ID
        // TODO 与之后验证时获取到的 Session ID 不同，导致验证码始终错误，暂时先采用这种方式，后期直接将 验证码保存到 Session 中
        // 反正采用 Spring Session Redis 后，Session也是保存在 Redis 中的
        // 造成这种问题的可能原因 ： https://www.cnblogs.com/imyjy/p/9187168.html
        session.setAttribute(WebConstant.VERIFY_CODE_KEY, session.getId());
        Image image = verifyCodeService.image(session.getId());
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
