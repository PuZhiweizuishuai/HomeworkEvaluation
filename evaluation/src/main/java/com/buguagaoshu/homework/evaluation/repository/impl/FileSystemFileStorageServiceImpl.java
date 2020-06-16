package com.buguagaoshu.homework.evaluation.repository.impl;

import com.buguagaoshu.homework.evaluation.model.FileModel;
import com.buguagaoshu.homework.evaluation.model.VditorFiles;
import com.buguagaoshu.homework.evaluation.repository.FileStorageRepository;
import com.buguagaoshu.homework.evaluation.utils.FileUtil;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-10 11:38
 * 将文件存储在本地
 */
@Repository
public class FileSystemFileStorageServiceImpl implements FileStorageRepository {
    private final FileUtil fileUtil;


    public FileSystemFileStorageServiceImpl(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @Override
    public Map<String, String> createUploadUrl(String objectName, String userId, Map<String, String> params) {
        return null;
    }

    @Override
    public VditorFiles save(MultipartFile[] files,
                            String userId,
                            Map<String, String> params,
                            HttpServletRequest request) {
        VditorFiles vditorFiles = new VditorFiles();
        vditorFiles.setCode(1);
        if (files.length > FileUtil.ONCE_UPLOAD_FILE_NUMBER) {

            vditorFiles.setMsg("一次最多上传文件不能超过9个！");
            return vditorFiles;
        }
        // 检查文件上传格式
        // TODO 如果时头像，格式必须是图片
        // TODO 检查上传作业时的上传权限
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (!fileUtil.checkSuffix(fileName)) {
                vditorFiles.setMsg("不支持的文件格式！");
                return vditorFiles;
            }
        }

        // 处理文件保存
        Map<String, Object> succMap = new HashMap<>(2);
        List<String> errFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            FileModel fileModel = fileUtil.filePath(fileName, userId, params.get("type"), params.get("homework"));

            String pathName = fileModel.getPath() + "/" + fileModel.getFilename();
            File dest = new File(fileModel.getPath());
            //判断文件父目录是否存在
            if (!dest.exists() && !dest.mkdirs()) {
                vditorFiles.setMsg("上传失败，请重试");
                return vditorFiles;
            }
            try {
                Files.copy(file.getInputStream(), Paths.get(fileModel.getPath(), fileModel.getFilename()));
                succMap.put(fileName, fileUtil.getWebsite() + "/" + pathName);
            } catch (Exception e) {
                errFiles.add(fileName);
            }
        }
        Map<String, Object> data = new HashMap<>(2);
        vditorFiles.setCode(0);
        vditorFiles.setMsg("上传成功");
        data.put("succMap", succMap);
        data.put("errFiles", errFiles);
        vditorFiles.setData(data);
        return vditorFiles;
    }

    @Override
    public Path load(String path, String filename) throws FileNotFoundException {
        Path filePath = Paths.get(path, filename);
        return filePath;
    }
}
