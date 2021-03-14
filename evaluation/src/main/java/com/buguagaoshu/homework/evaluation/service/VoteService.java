package com.buguagaoshu.homework.evaluation.service;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.entity.VoteEntity;
import com.buguagaoshu.homework.evaluation.entity.VoteLogEntity;
import com.buguagaoshu.homework.evaluation.vo.VoteVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-13 22:46
 */
public interface VoteService {
    /**
     * 创建投票
     * */
    void save(List<VoteVo> voteVos, Long articleId);

    /**
     * 更新投票数据
     * @return 返回投票结果
     * */
    ResponseDetails vote(VoteLogEntity voteLogEntity, HttpServletRequest request);


    /**
     * @return 查找投票结果
     * */
    List<VoteEntity> getVoteList(Long articleId);
}
