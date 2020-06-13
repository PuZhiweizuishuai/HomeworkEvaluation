package com.buguagaoshu.homework.evaluation.config;

import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.buguagaoshu.homework.evaluation.model.User;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;
import com.buguagaoshu.homework.evaluation.service.UserService;
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
        // TODO 支持邮箱和手机号登陆
        UserEntity userEntity = userService.getById(s);
        if (userEntity != null) {
            UserRoleEntity userRoleEntity = userRoleService.selectByUserId(userEntity.getUserId());
            User user = new User();
            BeanUtils.copyProperties(userEntity, user);
            user.setRole(userRoleEntity);
            // GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userRoleEntity.getRole());
            // 写入权限
            user.setAuthorities(AuthorityUtils.createAuthorityList(userRoleEntity.getRole()));
            return user;
        }
        throw new UsernameNotFoundException("用户名不存在，请检查用户名或注册！");
    }
}
