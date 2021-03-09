package com.buguagaoshu.homework.evaluation.service;



import com.buguagaoshu.homework.common.domain.DanmakuDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 弹幕表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-07 16:00:19
 */
public interface DanmakuService {


    /**
     * 获取弹幕列表
     * */
    List<Object> danmakuList(Long id, Integer max);

    /**
     * 保存弹幕
     * */
    ReturnCodeEnum saveDanmaku(DanmakuDetails danmakuVo, HttpServletRequest request);
}

