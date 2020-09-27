package com.buguagaoshu.homework.evaluation.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.DanmakuEntity;
import com.buguagaoshu.homework.evaluation.vo.DanmakuVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 弹幕表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-07 16:00:19
 */
public interface DanmakuService extends IService<DanmakuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取弹幕列表
     * */
    List<Object> danmakuList(Long id, Integer max);

    /**
     * 保存弹幕
     * */
    ReturnCodeEnum saveDanmaku(DanmakuVo danmakuVo, HttpServletRequest request);
}

