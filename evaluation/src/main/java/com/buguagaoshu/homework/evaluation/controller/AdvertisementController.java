package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.AdvertisementEntity;
import com.buguagaoshu.homework.evaluation.model.AdvertisementModel;
import com.buguagaoshu.homework.evaluation.service.AdvertisementService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-10 16:44
 */
@RestController
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @GetMapping("/admin/ad/list")
    public ResponseDetails list(@RequestParam Map<String, Object> params) {
        return ResponseDetails.ok().put("page", advertisementService.queryPage(params));
    }

    @PostMapping("/admin/ad/add")
    public ResponseDetails add(@RequestBody AdvertisementModel advertisementModel,
                               HttpServletRequest request) {
        AdvertisementEntity advertisementEntity = advertisementService.add(advertisementModel,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId());
        return ResponseDetails.ok().put("data", advertisementEntity);
    }

    @PostMapping("/admin/ad/update")
    public ResponseDetails update(@RequestBody AdvertisementModel advertisementModel,
                               HttpServletRequest request) {
        AdvertisementEntity advertisementEntity = advertisementService.updateAd(advertisementModel,
                JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId());
        if (advertisementEntity != null) {
            return ResponseDetails.ok().put("data", advertisementEntity);
        }
        return ResponseDetails.ok(ReturnCodeEnum.LACK_ID);
    }


    @PostMapping("/admin/ad/stop")
    public ResponseDetails stop(@RequestBody Long id) {

        return ResponseDetails.ok(advertisementService.stop(id));
    }
}
