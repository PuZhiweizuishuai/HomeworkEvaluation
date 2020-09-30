package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.UserLoginLogEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户登陆记录
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-04 12:28:16
 */
public interface UserLoginLogService extends IService<UserLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params, HttpServletRequest request);


    /**
     * 写入用户登录日志
     * @param request 请求信息
     * @param userId 用户ID
     * @return 保存结果
     * */
    boolean saveUserLoginLog(HttpServletRequest request, String userId);
}

