package com.buguagaoshu.homework.evaluation.feign;

import com.buguagaoshu.homework.common.domain.FileModel;
import com.buguagaoshu.homework.common.domain.ResponseDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-11 14:05
 */
@FeignClient(name = "OSS")
public interface OssFeignService {

    @PostMapping("/uploads/delete")
    ResponseDetails deleteCoursewareFile(FileModel fileModel);
}
