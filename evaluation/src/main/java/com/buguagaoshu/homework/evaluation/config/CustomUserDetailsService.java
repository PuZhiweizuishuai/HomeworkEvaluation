package com.buguagaoshu.homework.evaluation.config;

import com.buguagaoshu.homework.common.enums.UserStatusEnum;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.buguagaoshu.homework.evaluation.model.User;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-06 14:22
 * 自定义用户查找服务
 */
public class CustomUserDetailsService  implements UserDetailsService {

    private final UserService userService;

    private final UserRoleService userRoleService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomUserDetailsService(UserService userService, UserRoleService userRoleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        // TODO 支持手机号登陆
        // 如果是邮箱，采用邮箱登录
        UserEntity userEntity = null;
        if (s.contains("@")) {
            userEntity = userService.findByEmail(s);
        } else {
            userEntity = userService.getById(s);
        }

        if (userEntity != null) {
            // 是否是锁定状态
            if (userEntity.getStatus() == UserStatusEnum.LOCKING.getCode()) {
                // 还在锁定状态
                long endTime = userEntity.getStartLockTime() + userEntity.getLockTime();
                if (endTime > System.currentTimeMillis()) {
                    throw new UsernameNotFoundException("账号已被锁定，请在：" + TimeUtils.formatTime(endTime) + "锁定结束之后在登录！");
                } else {
                    UserEntity lockUser = new UserEntity();
                    lockUser.setStatus(UserStatusEnum.NORMAL.getCode());
                    lockUser.setUserId(userEntity.getUserId());
                    userService.updateById(lockUser);
                }
            }

            UserRoleEntity userRoleEntity = userRoleService.selectByUserId(userEntity.getUserId());
            User user = new User();
            BeanUtils.copyProperties(userEntity, user);
            user.setRole(userRoleEntity);

            // 写入权限
            user.setAuthorities(AuthorityUtils.createAuthorityList(userRoleEntity.getRole()));
            return user;
        }
        throw new UsernameNotFoundException("用户名不存在，请检查用户名或注册！");
    }
}
