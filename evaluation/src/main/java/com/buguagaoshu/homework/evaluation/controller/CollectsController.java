package com.buguagaoshu.homework.evaluation.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.CollectsEntity;
import com.buguagaoshu.homework.evaluation.service.CollectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-21 20:43
 * 收藏
 */
@RestController
public class CollectsController {
    private final CollectsService collectsService;

    @Autowired
    public CollectsController(CollectsService collectsService) {
        this.collectsService = collectsService;
    }


    @PostMapping("/collects/save")
    public ResponseDetails collects(@Valid @RequestBody CollectsEntity collectsEntity,
                                    HttpServletRequest request) {
        return ResponseDetails.ok().put("data", collectsService.save(collectsEntity, request));
    }


    /**
     * 获取收藏列表
     * */
    @GetMapping("/collects/list")
    public ResponseDetails list(@RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        PageUtils pageUtils = collectsService.queryPage(params, request);
        if (pageUtils == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_ROLE_OR_NO_FOUND);
        }
        return ResponseDetails.ok().put("data",pageUtils);
    }
}
