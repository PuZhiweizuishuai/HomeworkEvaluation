package com.buguagaoshu.homework.convertoffice.service;

import com.buguagaoshu.homework.common.domain.ConvertOfficeInfo;
import com.buguagaoshu.homework.convertoffice.utils.LibreOfficeUtil;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 15:53
 */
@Service
public class ConvertOfficeService {
    public void ConvertOfficeToPDF(ConvertOfficeInfo convertOfficeInfo) {
        File source = new File(convertOfficeInfo.getFilePath() + convertOfficeInfo.getFilename());

        File out = new File(convertOfficeInfo.getTargetFilePath());

        boolean success = LibreOfficeUtil.convertOffice2PDFSyncIsSuccess(source, out);
        if (success) {
            // 上传转换完成的文件
            System.out.println("上传");
        } else {
            // 保存失败信息，发送通知
            System.out.println("error!");
        }
    }
}
