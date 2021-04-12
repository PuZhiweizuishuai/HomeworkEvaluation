package com.buguagaoshu.homework.mail.service;

import com.buguagaoshu.homework.common.config.CustomConstant;
import com.buguagaoshu.homework.common.domain.MailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-12 19:11
 * 发送邮件
 */
@Service
public class SendMailService {
    private final JavaMailSender mailSender;

    private final SpringTemplateEngine thymeleaf;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    public SendMailService(JavaMailSender mailSender, SpringTemplateEngine thymeleaf) {
        this.mailSender = mailSender;
        this.thymeleaf = thymeleaf;
    }

    public void sendMessage(MailDetails mailDetails, String emailText) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(from);
        helper.setTo(mailDetails.getTo());
        helper.setSubject(mailDetails.getTitle());
        helper.setText(emailText, true);
        mailSender.send(message);
    }


    public void sendVerifyCode(MailDetails mailDetails) throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable("name", mailDetails.getName());
        ctx.setVariable("code", mailDetails.getCode());
        ctx.setVariable("to", mailDetails.getTo());
        String emailText = thymeleaf.process("verifyCode.html", ctx);
        sendMessage(mailDetails, emailText);
    }

    public void sendCommon(MailDetails mailDetails) throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable("name", mailDetails.getName());
        ctx.setVariable("text", mailDetails.getText());
        ctx.setVariable("to", mailDetails.getTo());
        String emailText = thymeleaf.process("commonMail.html", ctx);
        sendMessage(mailDetails, emailText);
    }

    public void send(MailDetails mailDetails) throws MessagingException {
        if (mailDetails.getType() == CustomConstant.VERIFY_CODE_MAIL) {
            sendVerifyCode(mailDetails);
        } else {
            sendCommon(mailDetails);
        }
    }
}
