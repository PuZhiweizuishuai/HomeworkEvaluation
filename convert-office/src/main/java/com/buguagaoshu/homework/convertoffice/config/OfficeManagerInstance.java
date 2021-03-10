package com.buguagaoshu.homework.convertoffice.config;

import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 15:28
 */
@Component
public class OfficeManagerInstance {
    private static OfficeManager INSTANCE = null;

    private final LibreOfficeProperties libreOfficeProperties;

    @Autowired
    public OfficeManagerInstance(LibreOfficeProperties libreOfficeProperties) {
        this.libreOfficeProperties = libreOfficeProperties;
    }

    public static synchronized void start() {
        officeManagerStart();
    }

    @PostConstruct
    private void init() {
        int[] ports = new int[libreOfficeProperties.getPorts().size()];
        for (int i = 0; i < libreOfficeProperties.getPorts().size(); i++) {
            ports[i] = libreOfficeProperties.getPorts().get(i);
        }

        LocalOfficeManager.Builder builder = LocalOfficeManager.builder().install();
        if (System.getProperty("os.name").contains("Windows")) {
            builder.officeHome(libreOfficeProperties.getHome());
        }
        builder.portNumbers(ports);
        builder.taskExecutionTimeout(libreOfficeProperties.getTaskExecutionTimeoutMinutes() * 1000 * 60);
        builder.taskQueueTimeout(libreOfficeProperties.getTaskQueueTimeoutHours() * 1000 * 60 * 60);
        INSTANCE = builder.build();
        officeManagerStart();
    }

    private static void officeManagerStart() {
        if (INSTANCE.isRunning()) {
            return;
        }
        try {
            INSTANCE.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
