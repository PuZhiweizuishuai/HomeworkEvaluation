package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.config.BaseWebInfoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-13 18:32
 */
@RestController
public class WebSettingController {
    private final BaseWebInfoConfig baseWebInfoConfig;

    @Autowired
    public WebSettingController(BaseWebInfoConfig baseWebInfoConfig) {
        this.baseWebInfoConfig = baseWebInfoConfig;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin/web/update/info")
    public ResponseDetails updateWebInfo(@RequestBody BaseWebInfoConfig info) {
        if (!StringUtils.isEmpty(info.getName())) {
            this.baseWebInfoConfig.setName(info.getName());
        }
        if (!StringUtils.isEmpty(info.getLogo())) {
            this.baseWebInfoConfig.setLogo(info.getLogo());
        }
        if (!StringUtils.isEmpty(info.getFaviconUrl())) {
            this.baseWebInfoConfig.setFaviconUrl(info.getFaviconUrl());
        }
        if (info.getRegisterEmailCheck() == 0 || info.getRegisterEmailCheck() == 1) {
            this.baseWebInfoConfig.setRegisterEmailCheck(info.getRegisterEmailCheck());
        }
        if (info.getRegisterInvitationCode() == 0 || info.getRegisterInvitationCode() == 1) {
            this.baseWebInfoConfig.setRegisterInvitationCode(info.getRegisterInvitationCode());
        }
        return ResponseDetails.ok().put("data", this.baseWebInfoConfig);
    }
}
