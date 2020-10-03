package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.evaluation.dao.InviteCodeUseLogDao;
import com.buguagaoshu.homework.evaluation.entity.InviteCodeUseLogEntity;
import com.buguagaoshu.homework.evaluation.service.InviteCodeUseLogService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;


/**
 * @author Pu Zhiwei
 * */
@Service("inviteCodeUseLogService")
public class InviteCodeUseLogServiceImpl extends ServiceImpl<InviteCodeUseLogDao, InviteCodeUseLogEntity> implements InviteCodeUseLogService {


    @Override
    public PageUtils queryPage(Map<String, Object> params, String codeId) {
        QueryWrapper<InviteCodeUseLogEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("invitecode_id", codeId);
        wrapper.orderByDesc("use_time");
        IPage<InviteCodeUseLogEntity> page = this.page(
                new Query<InviteCodeUseLogEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }
}