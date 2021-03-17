package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.ThinkEntity;
import com.buguagaoshu.homework.evaluation.model.ThinkModel;
import com.buguagaoshu.homework.evaluation.vo.ThinkVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2021-03-16 23:24:52
 */
public interface ThinkService extends IService<ThinkEntity> {

    PageUtils queryPage(Map<String, Object> params);


    ThinkModel saveThink(ThinkVo thinkVo, HttpServletRequest request);
}

