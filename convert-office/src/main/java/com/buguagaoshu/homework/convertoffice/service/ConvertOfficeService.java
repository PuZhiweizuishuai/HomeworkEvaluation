package com.buguagaoshu.homework.convertoffice.service;

import com.buguagaoshu.homework.common.domain.ConvertOfficeInfo;
import com.buguagaoshu.homework.convertoffice.config.FileConstant;
import com.buguagaoshu.homework.convertoffice.repository.FileStorageRepository;
import com.buguagaoshu.homework.convertoffice.utils.LibreOfficeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 15:53
 */
@Service
@Slf4j
public class ConvertOfficeService {

    private final FileStorageRepository repository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public ConvertOfficeService(FileStorageRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void ConvertOfficeToPDF(ConvertOfficeInfo convertOfficeInfo) {
        log.info("课件 {} 文档转码流程开始！", convertOfficeInfo.getCoursewareId());
        File tempDir = new File(FileConstant.TEMP);
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            log.error("请检查要写入文件目录的权限！！！");
            return;
        }
        boolean success = false;
        // 下载文件
        log.info("开始下载需要转换的文档： {}", convertOfficeInfo.getFilePath() + "/" +convertOfficeInfo.getFilename());
        if (repository.downloadFileToTemp(
                convertOfficeInfo.getFilePath() + "/" +convertOfficeInfo.getFilename(),
                convertOfficeInfo.getFilename())) {
            // 转码
            File source = new File(FileConstant.TEMP + convertOfficeInfo.getFilename());
            File out = new File(FileConstant.TEMP + convertOfficeInfo.getFilename() + ".pdf");
            success = LibreOfficeUtil.convertOffice2PDFSyncIsSuccess(source, out);
            log.info("转换完成,输出目录为：{}", out.getPath());
        }
        if (success) {
            // 上传转换完成的文件
            log.info("上传转码后的文件：{}", convertOfficeInfo.getFilePath() + "/" +  convertOfficeInfo.getFilename() + ".pdf");
            if (repository.uploadFile(
                    FileConstant.TEMP + convertOfficeInfo.getFilename() + ".pdf",
                    convertOfficeInfo.getFilePath() + "/" +  convertOfficeInfo.getFilename() + ".pdf")) {
                // 删除本地文件
                boolean d1 = repository.deleteTempFile(FileConstant.TEMP + convertOfficeInfo.getFilename() + ".pdf");
                boolean d2 = repository.deleteTempFile(FileConstant.TEMP + convertOfficeInfo.getFilename());
                if (d1 && d2) {
                    kafkaTemplate.send(
                            "ConvertMessage",
                            convertOfficeInfo.getCoursewareId() + "#T#" + convertOfficeInfo.getUserID() + "#" + convertOfficeInfo.getUsername() + "#" + convertOfficeInfo.getTypeMsg());
                    log.info("课件 {} 文档转码流程完成！", convertOfficeInfo.getCoursewareId());
                    return;
                }
            }
        } else {
            // 保存失败信息，发送通知
            log.error("课件 {} 转码失败", convertOfficeInfo.getCoursewareId());
        }
        kafkaTemplate.send("ConvertMessage", convertOfficeInfo.getCoursewareId() + "#F#转码失败");
    }
}
