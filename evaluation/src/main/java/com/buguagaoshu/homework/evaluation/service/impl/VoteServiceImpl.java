package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.UserVoteChoices;
import com.buguagaoshu.homework.evaluation.entity.VoteEntity;
import com.buguagaoshu.homework.evaluation.entity.VoteLogEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import com.buguagaoshu.homework.evaluation.model.VoteMode;
import com.buguagaoshu.homework.evaluation.repository.VoteLogRepository;
import com.buguagaoshu.homework.evaluation.repository.VoteRepository;
import com.buguagaoshu.homework.evaluation.service.VoteService;
import com.buguagaoshu.homework.evaluation.utils.JwtUtil;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;
import com.buguagaoshu.homework.evaluation.vo.VoteVo;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-13 22:48
 */
@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    private final VoteLogRepository voteLogRepository;

    private final MongoTemplate mongoTemplate;

    public VoteServiceImpl(VoteRepository voteRepository, VoteLogRepository voteLogRepository, MongoTemplate mongoTemplate) {
        this.voteRepository = voteRepository;
        this.voteLogRepository = voteLogRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(List<VoteVo> voteVos, Long articleId) {
        if (voteVos != null && voteVos.size() != 0) {
            if (voteVos.size() > 20) {
                throw new UserDataFormatException("当前最大只支持创建20个投票！");
            }
            List<VoteEntity> voteEntities = new ArrayList<>();
            for (VoteVo voteVo : voteVos) {
                voteVo.setArticleId(articleId);
                voteEntities.add(saveOneCheck(voteVo));
            }
            voteRepository.saveAll(voteEntities);
        }
    }

    public VoteEntity saveOneCheck(VoteVo voteVo) {
        if (voteVo.getChoices().size() < 2) {
            throw new UserDataFormatException("至少需要两个选项！");
        }
        if (voteVo.getType() == 1) {
            if (voteVo.getMaxChoice() > voteVo.getChoices().size()) {
                throw new UserDataFormatException("最大选项不能超过选项数！");
            }
        }
        VoteEntity voteEntity = new VoteEntity();
        BeanUtils.copyProperties(voteVo, voteEntity);
        voteEntity.setCreateTime(System.currentTimeMillis());
        Map<String, Long> map = new HashMap<>(voteVo.getChoices().size());
        voteVo.getChoices().forEach((v) -> {
            map.put(v, 0L);
        });
        if (!StringUtils.isEmpty(voteVo.getEndTime())) {
            voteEntity.setEndTime(TimeUtils.parseTime(voteVo.getEndTime()));
            if (voteEntity.getEndTime() < System.currentTimeMillis()) {
                throw new UserDataFormatException("结束时间必须在当前时间之后！");
            }
        } else {
            voteEntity.setEndTime(0L);
        }

        voteEntity.setChoices(map);
        voteEntity.setVoteCount(0L);
        voteRepository.save(voteEntity);
        return voteEntity;
    }

    @Override
    public ResponseDetails vote(VoteLogEntity voteLogEntity, HttpServletRequest request) {
        Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
        List<VoteEntity> voteEntityList = getVoteList(voteLogEntity.getArticleId());
        if (voteEntityList == null) {
            return ResponseDetails.ok(ReturnCodeEnum.NOO_FOUND).put("data", "投票不存在！");
        }
        if (voteEntityList.size() != voteLogEntity.getChoices().size()) {
            throw new UserDataFormatException("投票数据与系统数据不一致！");
        }

        VoteLogEntity voteLog = voteLogRepository.findVoteLogEntityByArticleIdAndUserId(voteLogEntity.getArticleId(), user.getId());
        if (voteLog != null) {
            throw new UserDataFormatException("你已经参与过此投票了！");
        }

        voteLogEntity.setUserId(user.getId());
        voteLogEntity.setCreateTime(System.currentTimeMillis());

        Map<String, VoteEntity> voteEntityMap =
                voteEntityList
                        .stream()
                        .collect(Collectors.toMap(voteEntity -> voteEntity.getId().toString(), v -> v));
        // 检查
        checkChoice(voteLogEntity.getChoices(), voteEntityMap);
        // TODO 优化执行方法，批量更新
        for (UserVoteChoices s : voteLogEntity.getChoices()) {
            updateOne(s, voteEntityMap.get(s.getId()));
        }

        voteLogRepository.save(voteLogEntity);
        return ResponseDetails.ok().put("data", voteLogEntity);
    }


    public void updateOne(UserVoteChoices voteChoices, VoteEntity voteEntity) {
        Query query = new Query(Criteria.where("id").is(voteEntity.getId()));
        Update update = new Update();
        if (voteEntity.getType() == 1) {
            for (String s : voteChoices.getChoices()) {
                update.inc("choices." + s, 1);
            }
        } else {
            update.inc("choices." + voteChoices.getChoices().get(0), 1);
        }
        update.inc("voteCount", 1);
        mongoTemplate.upsert(query, update, VoteEntity.class);
    }

    /**
     * 检查用户提交的选项是否在投票选项内
     */
    public void checkChoice(List<UserVoteChoices> choices, Map<String, VoteEntity> voteEntityMap) {
        for (UserVoteChoices s : choices) {
            VoteEntity voteEntity = voteEntityMap.get(s.getId());
            if (voteEntity == null) {
                throw new UserDataFormatException("所提交的投票ID有误！");
            }
            long time = System.currentTimeMillis();
            if (voteEntity.getEndTime() != 0) {
                if (time > voteEntity.getEndTime()) {
                    throw new UserDataFormatException("投票已经结束，当前无法进行投票！");
                }
            }
            // 检查选项
            // 多选
            if (voteEntity.getType() == 1) {
                if (choices.size() > voteEntity.getMaxChoice()) {
                    throw new UserDataFormatException("提交的选项超过了当前限制的最大数量！");
                }
                // 检查
                for (String choice : s.getChoices()) {
                    if (voteEntity.getChoices().get(choice) == null) {
                        throw new UserDataFormatException("不存在选项：" + choice);
                    }
                }
                // 单选
            } else {
                String c = s.getChoices().get(0);
                if (voteEntity.getChoices().get(c) == null) {
                    throw new UserDataFormatException("不存在选项：" + c);
                }
            }
        }
    }

    @Override
    public List<VoteEntity> getVoteList(Long articleId) {
        return voteRepository.findVoteEntityByArticleId(articleId);
    }

    @Override
    public List<VoteMode> getVoteModeList(Long articleId) {
        List<VoteMode> list = new ArrayList<>();
        List<VoteEntity> voteList = getVoteList(articleId);
        if (voteList == null) {
            return list;
        }
        voteList.forEach((v) -> {
            VoteMode voteMode = new VoteMode();
            BeanUtils.copyProperties(v, voteMode);
            voteMode.setId(v.getId().toString());
            list.add(voteMode);
        });
        return list;
    }

    @Override
    public VoteLogEntity voteLogEntity(Long articleId, String userId) {
        return voteLogRepository.findVoteLogEntityByArticleIdAndUserId(articleId, userId);
    }
}
