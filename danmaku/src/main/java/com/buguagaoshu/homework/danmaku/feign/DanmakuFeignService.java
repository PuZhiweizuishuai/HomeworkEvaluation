package com.buguagaoshu.homework.danmaku.feign;

import com.buguagaoshu.homework.common.domain.DanmakuDetails;
import com.buguagaoshu.homework.common.domain.ResponseDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-09 21:17
 */
@FeignClient(name = "Evaluation")
public interface DanmakuFeignService {
    /**
     * 调用远程发送保存弹幕的服务
     * */
    @PostMapping("/service/danmakus/check")
    ResponseDetails saveCheck(@RequestBody DanmakuDetails danmakuVo);
}
