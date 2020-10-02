package com.buguagaoshu.homework.evaluation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.evaluation.entity.InviteCodeEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 邀请码
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-06-03 22:57:42
 */
public interface InviteCodeService extends IService<InviteCodeEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 创建邀请码
     *
     * @param inviteCodeEntity 邀请码类型
     * @param request          请求的用户
     * @return 创建成功的邀请码
     */
    InviteCodeEntity create(InviteCodeEntity inviteCodeEntity, HttpServletRequest request);

    /**
     * 返回当前邀请码列表
     *
     * @param params  查询参数
     *                page: 页码
     *                limit： 每页显示数量
     *                course:  课程
     * @param request 获取当前用户
     * @return 分页后数据
     */
    PageUtils pageList(Map<String, Object> params, HttpServletRequest request);
}

