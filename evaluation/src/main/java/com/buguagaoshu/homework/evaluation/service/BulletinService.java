package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.BulletinEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 课程公告表
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-17 16:15:19
 */
public interface BulletinService extends IService<BulletinEntity> {

    PageUtils queryPage(Long id, Map<String, Object> params, HttpServletRequest request);


    /**
     * 保存课程公告
     * @param bulletinEntity 公告内容
     * @param request 获取用户信息
     * @return 保存结果
     * */
    ReturnCodeEnum saveBulletin(BulletinEntity bulletinEntity, HttpServletRequest request);
}

