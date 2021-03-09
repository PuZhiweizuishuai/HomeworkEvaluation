package com.buguagaoshu.homework.evaluation.controller;


import com.buguagaoshu.homework.common.domain.DanmakuDetails;
import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.service.DanmakuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;



/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-07 16:02
 */
@RestController
public class DanmakuController {

    private final DanmakuService danmakuService;

    @Autowired
    public DanmakuController(DanmakuService danmakuService) {
        this.danmakuService = danmakuService;
    }


    @PostMapping("/service/danmakus/check")
    public ResponseDetails post(@RequestBody DanmakuDetails danmakuVo,
                                HttpServletRequest request) {
        ReturnCodeEnum codeEnum = danmakuService.saveDanmaku(danmakuVo, request);

        if (codeEnum.equals(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND)) {
            return ResponseDetails.ok(codeEnum).put("code", 1);
        }
        return ResponseDetails.ok(codeEnum)
                .put("code", 0);
    }

}
