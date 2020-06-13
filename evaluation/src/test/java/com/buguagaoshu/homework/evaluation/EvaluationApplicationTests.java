package com.buguagaoshu.homework.evaluation;

import com.buguagaoshu.homework.common.enums.RoleTypeEnum;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.entity.UserRoleEntity;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.utils.InviteCodeUtil;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.unit.DataUnit;

@SpringBootTest
class EvaluationApplicationTests {
    @Autowired
    UserService userService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void contextLoads() {
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUserId("201741010102");
//        userEntity.setPassword(bCryptPasswordEncoder.encode("123456"));
//        userEntity.setEmail("948805382@qq.com");
//        userEntity.setPhoneNumber("18391703579");
//        userEntity.setUsername("蒲致威");
//        userEntity.setSex(0);
//        userEntity.setUserAvatarUrl("https://portrait.gitee.com/uploads/avatars/user/1741/5223124_puzhiweizuishuai_1578982745.png!avatar200");
//        userEntity.setUserIntro("系统测试账号");
//        userEntity.setCreateTime(System.currentTimeMillis());
//        userEntity.setLatestLoginTime(System.currentTimeMillis());
//        userEntity.setLatestLoginIp("192.168.1.106");
//        userEntity.setSchool("吉林师范大学");
//        userEntity.setMajor("计算机科学与技术");
//        userEntity.setGrade(2017L);
//        userService.save(userEntity);

//        UserRoleEntity userRoleEntity = new UserRoleEntity();
//        userRoleEntity.setUserId("201741010102");
//        userRoleEntity.setRole(RoleTypeEnum.ADMIN.getRole());
//        userRoleEntity.setCreateTime(System.currentTimeMillis());
//        userRoleService.save(userRoleEntity);
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUserId("201741010103");
//        userEntity.setPassword(bCryptPasswordEncoder.encode("123456"));
//        userService.updateById(userEntity);
//        System.out.println(InviteCodeUtil.createInviteCode());

    }

}
