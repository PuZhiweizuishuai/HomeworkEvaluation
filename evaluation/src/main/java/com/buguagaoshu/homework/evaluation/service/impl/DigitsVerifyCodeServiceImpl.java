package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.utils.VerifyCodeUtil;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.exception.VerifyFailedException;
import com.buguagaoshu.homework.evaluation.repository.VerifyCodeRepository;
import com.buguagaoshu.homework.evaluation.service.GenerateImageService;
import com.buguagaoshu.homework.evaluation.service.SendMessageService;
import com.buguagaoshu.homework.evaluation.service.VerifyCodeService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.security.SecureRandom;
import java.util.Objects;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2019-11-26 17:07
 * 验证码服务
 */
@Service
public class DigitsVerifyCodeServiceImpl implements VerifyCodeService {
    private final VerifyCodeRepository verifyCodeRepository;

    private final GenerateImageService generateImageService;

    private final SendMessageService sendMessageService;

    private final VerifyCodeUtil verifyCodeUtil;

    public static final long VERIFY_CODE_EXPIRE_TIMEOUT = 60000L;

    public static final long SEND_VERIFY_CODE_EXPIRE_TIMEOUT = 15L;

    public DigitsVerifyCodeServiceImpl(VerifyCodeRepository verifyCodeRepository, GenerateImageService generateImageService, SendMessageService sendMessageService, VerifyCodeUtil verifyCodeUtil) {
        this.verifyCodeRepository = verifyCodeRepository;
        this.generateImageService = generateImageService;
        this.sendMessageService = sendMessageService;
        this.verifyCodeUtil = verifyCodeUtil;
    }

    @Override
    public String randomDigitString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * @param string 验证码
     * @param type 验证码类型
     *             S 手机或邮箱发送的验证码，有效时间长
     *             W web 页面的图形验证码，时间短
     * */
    private static String appendTimestamp(String string, String type) {
        return string + "#" + System.currentTimeMillis() + "#" + type;
    }

    @Override
    public void send(String key, UserEntity userEntity, String email, HttpSession session) {
        String verifyCode = randomDigitString(verifyCodeUtil.getLen());
        String verifyCodeWithTimestamp = appendTimestamp(verifyCode, "S");
        session.setAttribute(email, verifyCodeWithTimestamp);
        // verifyCodeRepository.save(key, verifyCodeWithTimestamp, SEND_VERIFY_CODE_EXPIRE_TIMEOUT);
        sendMessageService.sendVerifyCode(key, verifyCode, userEntity, email);
    }

    @Override
    public void verify(String key, String code, HttpSession session) {
        String lastVerifyCodeWithTimestamp = (String) session.getAttribute(key);
        // 如果没有验证码，则随机生成一个
        if (lastVerifyCodeWithTimestamp == null) {
            lastVerifyCodeWithTimestamp = appendTimestamp(randomDigitString(verifyCodeUtil.getLen()), "W");
        }
        String[] lastVerifyCodeAndTimestamp = lastVerifyCodeWithTimestamp.split("#");
        String lastVerifyCode = lastVerifyCodeAndTimestamp[0];
        long timestamp = Long.parseLong(lastVerifyCodeAndTimestamp[1]);
        long expTime = VERIFY_CODE_EXPIRE_TIMEOUT;
        if ("S".equals(lastVerifyCodeAndTimestamp[2])) {
            expTime = VERIFY_CODE_EXPIRE_TIMEOUT * SEND_VERIFY_CODE_EXPIRE_TIMEOUT;
        }
        if (timestamp + expTime < System.currentTimeMillis()) {
            session.removeAttribute(key);
            throw new VerifyFailedException("验证码已过期！");
        } else if (!Objects.equals(code, lastVerifyCode)) {
            session.removeAttribute(key);
            throw new VerifyFailedException("验证码错误！");
        }
    }

    @Override
    public Image image(HttpSession session) {
        String verifyCode = randomDigitString(verifyCodeUtil.getLen());
        String verifyCodeWithTimestamp = appendTimestamp(verifyCode, "W");
        Image image = generateImageService.imageWithDisturb(verifyCode);
        // verifyCodeRepository.save(WebConstant.VERIFY_CODE_KEY, verifyCodeWithTimestamp);
        session.setAttribute(WebConstant.VERIFY_CODE_KEY, verifyCodeWithTimestamp);
        return image;
    }
}
