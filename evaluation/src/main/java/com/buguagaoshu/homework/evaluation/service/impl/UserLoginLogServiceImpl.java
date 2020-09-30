package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.IPv6Utils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.UserLoginLogDao;
import com.buguagaoshu.homework.evaluation.entity.UserLoginLogEntity;
import com.buguagaoshu.homework.evaluation.service.UserLoginLogService;

import javax.servlet.http.HttpServletRequest;


/**
 * @author puzhiwei
 */
@Service("userLoginLogService")
@Slf4j
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogDao, UserLoginLogEntity> implements UserLoginLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        QueryWrapper<UserLoginLogEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId());
        wrapper.orderByDesc("login_time");
        IPage<UserLoginLogEntity> page = this.page(
                new Query<UserLoginLogEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public boolean saveUserLoginLog(HttpServletRequest request, String userId) {
        UserLoginLogEntity userLoginLogEntity = new UserLoginLogEntity();
        // TODO IPv6 支持
        userLoginLogEntity.setLoginIp(IpUtil.getIpAddr(request));
        userLoginLogEntity.setLoginTime(System.currentTimeMillis());
        userLoginLogEntity.setLoginUa(request.getHeader("user-agent"));
        userLoginLogEntity.setUserId(userId);
        try {
            this.save(userLoginLogEntity);
            return true;
        } catch (Exception e) {
            log.error("用户 {} 的登录记录保存失败！ 错误原因： {}", userId, e.getMessage());
            return false;
        }

    }

}