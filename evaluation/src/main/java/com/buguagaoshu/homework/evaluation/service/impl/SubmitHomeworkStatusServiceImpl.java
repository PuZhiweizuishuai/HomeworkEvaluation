package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.enums.HomeworkSubmitStatusEnum;
import com.buguagaoshu.homework.evaluation.entity.HomeworkEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.homework.common.utils.PageUtils;
import com.buguagaoshu.homework.common.utils.Query;

import com.buguagaoshu.homework.evaluation.dao.SubmitHomeworkStatusDao;
import com.buguagaoshu.homework.evaluation.entity.SubmitHomeworkStatusEntity;
import com.buguagaoshu.homework.evaluation.service.SubmitHomeworkStatusService;

/**
 * @author Pu Zhiwei
 * */
@Service("submitHomeworkStatusService")
public class SubmitHomeworkStatusServiceImpl extends ServiceImpl<SubmitHomeworkStatusDao, SubmitHomeworkStatusEntity> implements SubmitHomeworkStatusService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SubmitHomeworkStatusEntity> page = this.page(
                new Query<SubmitHomeworkStatusEntity>().getPage(params),
                new QueryWrapper<SubmitHomeworkStatusEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public SubmitHomeworkStatusEntity findUserSubmit(String userId, Long homeworkId) {
        return this.getOne(new QueryWrapper<SubmitHomeworkStatusEntity>().eq("user_id", userId).eq("homework_id", homeworkId));
    }

    @Override
    public SubmitHomeworkStatusEntity saveSubmitStatus(HomeworkEntity homeworkEntity, String id, int status, String studentName) {
        long time = System.currentTimeMillis();
        SubmitHomeworkStatusEntity submitHomeworkStatusEntity = new SubmitHomeworkStatusEntity();
        submitHomeworkStatusEntity.setStudentName(studentName);
        submitHomeworkStatusEntity.setCreateTime(time);
        submitHomeworkStatusEntity.setHomeworkId(homeworkEntity.getId());
        submitHomeworkStatusEntity.setScore(0.0);
        submitHomeworkStatusEntity.setUpdateTime(time);
        submitHomeworkStatusEntity.setUserId(id);
        submitHomeworkStatusEntity.setStatus(status);
        this.save(submitHomeworkStatusEntity);
        return submitHomeworkStatusEntity;
    }

    @Override
    public List<SubmitHomeworkStatusEntity> submitUserList(Long homeworkID) {
        QueryWrapper<SubmitHomeworkStatusEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("homework_id", homeworkID);

        wrapper.and(
                status -> status.eq("status", HomeworkSubmitStatusEnum.SUBMIT.getCode())
                        .or()
                        .eq("status", HomeworkSubmitStatusEnum.COMPLETE.getCode())
        );
        wrapper.orderByDesc("submit_time");
        return list(wrapper);
    }

    @Override
    public List<SubmitHomeworkStatusEntity> teacherNoCommentSubmit(long homeworkId) {
        QueryWrapper<SubmitHomeworkStatusEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("homework_id", homeworkId);

        wrapper.and(
                status -> status.eq("status", HomeworkSubmitStatusEnum.SUBMIT.getCode())
        );
        wrapper.orderByDesc("submit_time");
        return list(wrapper);
    }

    @Override
    public void addCount(String col, Long submitId, int count) {
        this.baseMapper.addCount(col, submitId, count);
    }
}
