package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.UserRoleDao;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;

import javax.servlet.http.HttpServletRequest;


/**
 * @author puzhiwei
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleDao, UserRoleEntity> implements UserRoleService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserRoleEntity> page = this.page(
                new Query<UserRoleEntity>().getPage(params),
                new QueryWrapper<UserRoleEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public UserRoleEntity selectByUserId(String userId) {
        return baseMapper.selectRoleByUserId(userId);
    }

    @Override
    public ReturnCodeEnum alterUserRole(UserRoleEntity userRoleEntity, HttpServletRequest request) {
        UserRoleEntity old = this.baseMapper.selectRoleByUserId(userRoleEntity.getUserId());
        if (old == null) {
            return ReturnCodeEnum.USER_NOT_FIND;
        }
        String nowUser = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY).getId();
        if (nowUser.equals(old.getUserId())) {
            return ReturnCodeEnum.CANNOT_BE_ALTER_YOUR_ROLE;
        }
        if (RoleTypeEnum.check(userRoleEntity.getRole())) {
            old.setRole(userRoleEntity.getRole());
            old.setCreateTime(System.currentTimeMillis());

            old.setOperator(nowUser);
            this.updateById(old);
            return ReturnCodeEnum.SUCCESS;
        } else {
            return ReturnCodeEnum.USER_ROLE_BAD;
        }

    }

}
