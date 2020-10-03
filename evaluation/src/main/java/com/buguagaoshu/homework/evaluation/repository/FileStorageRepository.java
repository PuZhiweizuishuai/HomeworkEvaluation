package com.buguagaoshu.homework.evaluation.repository;

import com.buguagaoshu.homework.evaluation.model.VditorFiles;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-04-08 21:27
 * 文件保存接口
 */
public interface FileStorageRepository {
    /**
     * 仅在使用第三方云存储时有用
     * 生成客户端上传文件所需的URl
     * @param objectName 文件名字
     * @param userId 用户 ID
     * @param params 附加参数
     *               type：文件所属分区
     *               homework： 作业 ID
     *
     * @return 上传文件所需要的 URL
     * */
    Map<String, String> createUploadUrl(String objectName, String userId, Map<String, String> params);

    /**
     * 上传文件，通过后台系统转发到 MinIO
     * @param files 文件列表
     * @param userId 用户 ID
     * @param params 附加参数
     *               type：文件所属分区
     *               homework： 作业 ID
     * @param request 请求对象
     * @return {
     *  "msg": "",
     *  "code": 0,
     *  "data": {
     *  "errFiles": ['filename', 'filename2'],
     *  "succMap": {
     *    "filename3": "filepath3",
     *    "filename3": "filepath3"
     *    }
     *  }
     * }
     * TODO 采用让前端直接上传到 MinIo 等对象存储需要自定义前端的上传方法，目前为了快速，就先选后台转发吧
     * */
    VditorFiles save(MultipartFile[] files,
                     String userId,
                     Map<String, String> params,
                     HttpServletRequest request);


    /**
     * 加载文件资源
     * @param path 请求路径
     * @param filename 文件名
     * @return 资源路径
     * */
    Path load(String path,String filename) throws FileNotFoundException;


    /**
     * 课件上传专属接口
     * */
    Map<String, String> save(MultipartFile file, String course, HttpServletRequest request);
}
