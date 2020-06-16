package com.buguagaoshu.homework.evaluation.utils;

import com.buguagaoshu.homework.evaluation.config.BaseWebInfoConfig;
import com.buguagaoshu.homework.evaluation.exception.FilePathException;
import com.buguagaoshu.homework.evaluation.model.FileModel;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-09 14:46
 */
@Component
public class FileUtil {
    private final BaseWebInfoConfig baseWebInfoConfig;

    /**
     * 一次最多上传的文件数量
     * */
    public final static int ONCE_UPLOAD_FILE_NUMBER = 9;

    private String website = "127.0.0.1:8080";

    public FileUtil(BaseWebInfoConfig baseWebInfoConfig) {
        this.baseWebInfoConfig = baseWebInfoConfig;
        this.website = baseWebInfoConfig.getWebsite();
    }

    /**
     * 文件存储路径
     * /uploads/用户ID/日期/UUID-文件名
     * 头像特殊处理
     * /uploads/avatars
     * */
    public FileModel filePath(String objectName, String userId, String type, String homework) {
        if (type == null) {
            type = "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        FileModel fileModel = new FileModel();
        fileModel.setFilename(InviteCodeUtil.createInviteCode() + getFileSuffix(objectName));
        if("avatars".equals(type)) {
            fileModel.setPath("uploads/avatars/" + userId);
            return fileModel;
        } else if ("homework".equals(type)) {
            if (homework == null) {
                throw new FilePathException("文件请求路径异常");
            }
            fileModel.setPath("uploads/homework/" + homework + "/" + userId);
            return fileModel;
        } else {
            fileModel.setPath("uploads/file/" + userId + "/" + simpleDateFormat.format(new Date()));
            return fileModel;
        }
    }

    public String getFileSuffix(String filename) {
        int number = filename.lastIndexOf(".");
        if (number <= 0) {
            return "";
        }
        return filename.substring(number).toLowerCase();
    }

    public boolean checkSuffix(String filename) {
        if (baseWebInfoConfig.getFileSuffix() == null || "".equals(baseWebInfoConfig.getFileSuffix())) {
            return true;
        }
        String[] suf = baseWebInfoConfig.getFileSuffix().split(",");
        for (String s : suf) {
            if (s.equals(getFileSuffix(filename))) {
                return true;
            }
        }
        return false;
    }

    public String getWebsite() {
        return website;
    }
}
