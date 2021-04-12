package com.buguagaoshu.homework.mail.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.mail.service.SendMailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-12 19:57
 */
@RestController
public class MailController {
    private final SendMailService sendMail;

    public MailController(SendMailService sendMail) {
        this.sendMail = sendMail;
    }

    @GetMapping("/mail/send")
    public ResponseDetails send() throws MessagingException {

        return ResponseDetails.ok();
    }
}
