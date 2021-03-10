package com.buguagaoshu.homework.convertoffice.utils;

import com.buguagaoshu.homework.convertoffice.config.OfficeManagerInstance;
import org.jodconverter.local.JodConverter;
import java.io.File;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 15:27
 */
public class LibreOfficeUtil {
    /**
     * 利用 JodConverter 将 Offfice 文档转换为 PDF（要依赖 LibreOffice），该转换为同步转换，返回时就已经转换完成
     */
    public static boolean convertOffice2PDFSyncIsSuccess(File sourceFile, File targetFile) {
        try {
            OfficeManagerInstance.start();
            JodConverter.convert(sourceFile).to(targetFile).execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 利用 LibreOffice 将 Office 文档转换成 PDF，该转换是异步的，返回时，转换可能还在进行中，转换是否有异常也未可知
     * @param filePath       目标文件地址
     * @param targetFilePath 输出文件夹
     * @return 子线程执行完毕的返回值
     */
    public static int convertOffice2PDFAsync(String filePath, String fileName, String targetFilePath) throws Exception {
        String command;
        int exitStatus;
        String osName = System.getProperty("os.name");
        String outDir = targetFilePath.length() > 0 ? " --outdir " + targetFilePath : "";

        if (osName.contains("Windows")) {
            command = "cmd /c cd /d " + filePath + " && start soffice --headless --invisible --convert-to pdf ./" + fileName + outDir;
        } else {
            command = "libreoffice --headless --invisible --convert-to pdf:writer_pdf_Export " + filePath + fileName + outDir;
        }
        exitStatus = executeOSCommand(command);
        return exitStatus;
    }

    /**
     * 调用操作系统的控制台，执行 command 指令
     * 执行该方法时，并没有等到指令执行完毕才返回，而是执行之后立即返回，返回结果为 0，只能说明正确的调用了操作系统的控制台指令，但执行结果如何，是否有异常，在这里是不能体现的，所以，更好的姿势是用同步转换功能。
     */
    private static int executeOSCommand(String command) throws Exception {
        Process process;

        // 转换需要时间，比如一个 3M 左右的文档大概需要 8 秒左右，但实际测试时，并不会等转换结束才执行下一行代码，而是把执行指令发送出去后就立即执行下一行代码了。
        process = Runtime.getRuntime().exec(command);
        int exitStatus = process.waitFor();

        if (exitStatus == 0) {
            exitStatus = process.exitValue();
        }
        // 销毁子进程
        process.destroy();
        return exitStatus;
    }
}
