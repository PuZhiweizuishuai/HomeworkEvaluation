package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.entity.UserEntity;
import com.buguagaoshu.homework.evaluation.model.ThinkModel;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.service.VerifyCodeService;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.vo.ThinkVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.ThinkDao;
import com.buguagaoshu.homework.evaluation.entity.ThinkEntity;
import com.buguagaoshu.homework.evaluation.service.ThinkService;

import javax.servlet.http.HttpServletRequest;


@Service("thinkService")
public class ThinkServiceImpl extends ServiceImpl<ThinkDao, ThinkEntity> implements ThinkService {

    private final VerifyCodeService verifyCodeService;

    private final UserService userService;

    private final ObjectMapper objectMapper;

    @Autowired
    public ThinkServiceImpl(VerifyCodeService verifyCodeService, UserService userService, ObjectMapper objectMapper) {
        this.verifyCodeService = verifyCodeService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ThinkEntity> page = this.page(
                new Query<ThinkEntity>().getPage(params),
                new QueryWrapper<ThinkEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public ThinkModel saveThink(ThinkVo thinkVo, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        ThinkEntity thinkEntity = new ThinkEntity();
        BeanUtils.copyProperties(thinkVo, thinkEntity);
        thinkEntity.initData();
        thinkEntity.setAuthorId(user.getId());
        thinkEntity.setIp(IpUtil.getIpAddr(request));
        thinkEntity.setUa(IpUtil.getUa(request));
        if (thinkVo.getForward() != null) {
            // TODO 转发功能
        }
        thinkEntity.setFiles(objectToString(thinkVo));
        this.save(thinkEntity);
        ThinkModel thinkModel = new ThinkModel();
        BeanUtils.copyProperties(thinkEntity, thinkModel);
        thinkModel.setFiles(thinkVo.getFiles());
        UserEntity userEntity = userService.getById(user.getId());
        userEntity.clean();
        thinkModel.setUser(userEntity);
        return thinkModel;
    }

    public String objectToString(ThinkVo thinkVo) {
        try {
            return objectMapper.writeValueAsString(thinkVo.getFiles());
        } catch (Exception e) {
            return "[]";
        }
    }


    public List<String> stringToObj(String str) {
        try {
            return objectMapper.readValue(str, List.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}