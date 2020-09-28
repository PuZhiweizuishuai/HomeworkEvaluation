package com.buguagaoshu.homework.evaluation.controller;


import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.service.DanmakuService;
import com.buguagaoshu.homework.evaluation.vo.DanmakuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-07 16:02
 * TODO 使用消息队列，拆分弹幕服务，达到读写分离
 */
@RestController
public class DanmakuController {

    private final DanmakuService danmakuService;

    @Autowired
    public DanmakuController(DanmakuService danmakuService) {
        this.danmakuService = danmakuService;
    }


    /**
     * TODO 加入缓存
     * */
    @GetMapping("/danmaku/v3")
    public ResponseDetails get(@RequestParam("id") Long id,
                               @RequestParam("max") Integer max) {
        return ResponseDetails.ok().put("data", danmakuService.danmakuList(id, max))
                .put("code", 0);
    }


    @PostMapping("/danmaku/v3")
    public ResponseDetails post(@RequestBody DanmakuVo danmakuVo,
                                HttpServletRequest request) {
        ReturnCodeEnum codeEnum = danmakuService.saveDanmaku(danmakuVo, request);
        if (codeEnum.equals(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND)) {
            return ResponseDetails.ok(codeEnum).put("code", 1);
        }
        return ResponseDetails.ok(codeEnum)
                .put("code", 0);
    }

}
