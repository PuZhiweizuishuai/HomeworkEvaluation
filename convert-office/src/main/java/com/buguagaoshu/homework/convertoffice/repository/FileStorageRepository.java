package com.buguagaoshu.homework.convertoffice.repository;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 17:01
 */
public interface FileStorageRepository {
    /**
     * 下载文件到缓存目录
     * */
    boolean downloadFileToTemp(String filPath, String filename);


    /**
     * 上传转换完成的PDF文件
     */
    boolean uploadFile(String localFilePath, String ossPath);


    /**
     * 删除本地缓存文件
     * */
    boolean deleteTempFile(String filePath);
}
