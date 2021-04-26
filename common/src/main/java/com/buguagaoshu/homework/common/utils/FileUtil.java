package com.buguagaoshu.homework.common.utils;


import com.buguagaoshu.homework.common.domain.FileModel;
import com.buguagaoshu.homework.common.exception.FilePathException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-09 14:46
 */
@Component
public class FileUtil {

    /**
     * 一次最多上传的文件数量
     */
    public final static int ONCE_UPLOAD_FILE_NUMBER = 9;

    public final static Long MAX_FILE_SIZE = 20971520L;

    public FileUtil() {
    }

    public static final String PDF = ".pdf";

    public static final String[] VIDEO_TYPE = {
            ".mp4", ".mkv", ".webm", ".mov", ".mpeg", ".m4v", ".avi", ".flv"
    };

    public static final String[] VOICE_TYPE = {
            ".mp3", ".wav", ".flac", ".ape", ".aac"
    };

    public static final String[] PHOTO_TYPE = {
            ".jpg", ".jpeg", ".png", ".gif", ".ico"
    };


    public static final String[] DOC_FILE = {
            ".doc", ".xls", ".ppt", ".docx", ".xlsx", ".pptx", ".odg", ".otg", ".fodg", ".odp", ".otp",
            ".pps", ".ods", ".xlsm", ".odt", ".ott", ".dot", ".dotx", ".docm"
    };

    public final static int OFFICE_CODE = 3;

    /**
     * 文件存储路径
     * /uploads/用户ID/日期/UUID-文件名
     * 头像特殊处理
     * /uploads/avatars
     */
    public FileModel filePath(String objectName, String userId, String type, String homework) {
        if (type == null) {
            type = "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        FileModel fileModel = new FileModel();
        fileModel.setFilename(InviteCodeUtil.createInviteCode() + getFileSuffix(objectName));
        if ("avatars".equals(type)) {
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

    public FileModel coursewareFilePath(String filename, String course, String userId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        FileModel fileModel = new FileModel();
        fileModel.setFilename(InviteCodeUtil.createInviteCode() + getFileSuffix(filename));
        fileModel.setPath("uploads/courseware/" + course + "/"  + userId + "/" + simpleDateFormat.format(new Date()));
        return fileModel;
    }

    public String getFileSuffix(String filename) {
        int number = filename.lastIndexOf(".");
        if (number <= 0) {
            return "";
        }
        return filename.substring(number).toLowerCase();
    }

    public int fileTypeCode(String filename) {
        String suffix = getFileSuffix(filename);
        // PDF 和 视频文件均可预览
        for (String s : VIDEO_TYPE) {
            if (s.equals(suffix)) {
                return 1;
            }
        }

        if (PDF.equals(suffix)) {
            return 2;
        }

        for (String s : VOICE_TYPE) {
            if (s.equals(suffix)) {
                return 4;
            }
        }

        for (String s : DOC_FILE) {
            if (s.equals(suffix)) {
                return 3;
            }
        }

        for (String s: PHOTO_TYPE) {
            if (s.equals(suffix)) {
                return 5;
            }
        }

        // 不支持预览，需要下载的文件格式
        return 0;
    }

    public boolean checkSuffix(String filename) {
        return true;
//        if (baseWebInfoConfig.getFileSuffix() == null || "".equals(baseWebInfoConfig.getFileSuffix())) {
//            return true;
//        }
//        String[] suf = baseWebInfoConfig.getFileSuffix().split(",");
//        for (String s : suf) {
//            if (s.equals(getFileSuffix(filename))) {
//                return true;
//            }
//        }
//        return false;
    }

    public String getNewFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    public String removeApiWithName(String url) {
        return url.substring(4, url.lastIndexOf("/"));
    }

}
