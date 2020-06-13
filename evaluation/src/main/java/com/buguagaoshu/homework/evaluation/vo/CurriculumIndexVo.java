package com.buguagaoshu.homework.evaluation.vo;

import com.buguagaoshu.homework.evaluation.entity.AdvertisementEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-11 22:17
 * 课程页主要的数据
 */
@Data
public class CurriculumIndexVo {
    private List<AdvertisementEntity> carousel;


    private List<TagAndCourseVo> tagAndCourseVos;
}


