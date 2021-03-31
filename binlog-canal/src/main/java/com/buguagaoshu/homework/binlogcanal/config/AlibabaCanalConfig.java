package com.buguagaoshu.homework.binlogcanal.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.buguagaoshu.homework.binlogcanal.client.ClusterCanalClient;
import com.buguagaoshu.homework.binlogcanal.service.BinLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-02-27 23:29
 */
@Configuration
@Slf4j
public class AlibabaCanalConfig {
    private final AlibabaCanalProperties properties;

    private final BinLogService binLogService;

    @Autowired
    public AlibabaCanalConfig(AlibabaCanalProperties properties, BinLogService binLogService) {
        this.properties = properties;
        this.binLogService = binLogService;
    }

    /**
     * 初始化，连接 Canal 服务器
     * */
    @Bean
    public CommandLineRunner dataLoader() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                // 创建 Canal 连接
                // 基于固定canal server的地址，建立链接，其中一台server发生crash，可以支持failover
                List<InetSocketAddress> socketAddresses = new ArrayList<>();
                for (String host : properties.getHost()) {
                    InetSocketAddress address = new InetSocketAddress(getHost(host), getPort(host));
                    socketAddresses.add(address);
                }
                CanalConnector connector = CanalConnectors.newClusterConnector(
                        socketAddresses,
                        properties.getDestination(),
                        properties.getUsername(),
                        properties.getPassword()
                );
                final ClusterCanalClient client = new ClusterCanalClient(properties.getDestination(), connector, binLogService);
                client.start();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        log.info("## stop the canal client");
                        client.stop();
                    } catch (Throwable e) {
                        log.warn("##something goes wrong when stopping canal:", e);
                    } finally {
                        log.info("## canal client is down.");
                    }
                }));
            }
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    public String getHost(String host) {
        return host.substring(0, host.lastIndexOf(":"));
    }

    public int getPort(String host) {
        return Integer.parseInt(host.substring(host.lastIndexOf(":") + 1));
    }

}
