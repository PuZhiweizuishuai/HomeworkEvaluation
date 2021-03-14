package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.entity.VoteEntity;
import com.buguagaoshu.homework.evaluation.entity.VoteLogEntity;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        voteEntity.setEndTime(TimeUtils.parseTime(voteVo.getEndTime()));
        voteEntity.setChoices(map);
        voteEntity.setVoteCount(0L);
        voteRepository.save(voteEntity);
        return voteEntity;
    }

    @Override
    public ResponseDetails vote(VoteLogEntity voteLogEntity, HttpServletRequest request) {
        // Claims user = JwtUtil.getNowLoginUser(request, TokenAuthenticationHelper.SECRET_KEY);
//        VoteEntity voteEntity = getVote(voteLogEntity.getVoteId());
//        if (voteEntity == null) {
//            return ResponseDetails.ok(ReturnCodeEnum.NOO_FOUND).put("data", "投票不存在！");
//        }
//        VoteLogEntity voteLog = voteLogRepository.findVoteLogEntityByVoteIdAndUserId(voteLogEntity.getVoteId(), "201741010102");
//        if (voteLog != null) {
//            throw new UserDataFormatException("你已经参与过此投票了！");
//        }
//        voteLogEntity.setUserId("201741010102");
//        voteLogEntity.setCreateTime(System.currentTimeMillis());
//
//
//        Query query = new Query(Criteria.where("id").is(voteLogEntity.getVoteId()));
//        Update update = new Update();
//        if (voteEntity.getType() == 1) {
//            if (voteLogEntity.getChoice().size() <= voteEntity.getMaxChoice()) {
//                for (String s : voteLogEntity.getChoice()) {
//                    update.inc("choices." + s, 1);
//
//
//                }
//            } else {
//                throw new UserDataFormatException("超过最大可选择选项！");
//            }
//        } else {
//            update.inc("choices." + voteLogEntity.getChoice().get(0), 1);
//        }
//
//        update.inc("voteCount", 1);
//        mongoTemplate.upsert(query, update, VoteEntity.class);
//        voteLogRepository.save(voteLogEntity);
//        return ResponseDetails.ok().put("data", voteEntity);
        return null;
    }

    @Override
    public List<VoteEntity> getVoteList(Long articleId) {
        return voteRepository.findVoteEntityByArticleId(articleId);
    }
}
