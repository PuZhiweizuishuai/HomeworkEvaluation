package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.evaluation.dao.InviteCodeDao;
import com.buguagaoshu.homework.evaluation.entity.InviteCodeEntity;
import com.buguagaoshu.homework.evaluation.service.InviteCodeService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;




/**
 * @author puzhiwei
 */
@Service("inviteCodeService")
public class InviteCodeServiceImpl extends ServiceImpl<InviteCodeDao, InviteCodeEntity> implements InviteCodeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<InviteCodeEntity> page = this.page(
                new Query<InviteCodeEntity>().getPage(params),
                new QueryWrapper<InviteCodeEntity>()
        );

        return new PageUtils(page);
    }

}