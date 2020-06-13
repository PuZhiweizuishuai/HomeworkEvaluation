package com.buguagaoshu.homework.evaluation.service.impl;

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


/**
 * @author puzhiwei
 */
@Service("userLoginLogService")
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogDao, UserLoginLogEntity> implements UserLoginLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserLoginLogEntity> page = this.page(
                new Query<UserLoginLogEntity>().getPage(params),
                new QueryWrapper<UserLoginLogEntity>()
        );

        return new PageUtils(page);
    }

}