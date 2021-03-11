package com.buguagaoshu.homework.oss.repository;

import com.buguagaoshu.homework.common.domain.VditorFiles;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 17:01
 */
public interface FileStorageRepository {
    /**
     * 普通文件上传接口
     * */
    VditorFiles save(MultipartFile[] files,
                     String userId,
                     Map<String, String> params,
                     HttpServletRequest request);

    /**
     * 课件上传专属接口
     * */
    Map<String, String> save(MultipartFile file, String course, HttpServletRequest request);



    String getFileUrl(String path);


    Boolean delete(String path);
}
