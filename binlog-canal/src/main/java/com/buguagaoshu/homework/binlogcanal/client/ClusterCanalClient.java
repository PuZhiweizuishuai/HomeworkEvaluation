package com.buguagaoshu.homework.binlogcanal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.buguagaoshu.homework.binlogcanal.service.BinLogService;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-02-27 23:42
 * 连接到 Alibaba Canal
 */
public class ClusterCanalClient extends AbstractCanalClient {
    public ClusterCanalClient(String destination, BinLogService binLogService) {
        super(destination, binLogService);
    }

    public ClusterCanalClient(String destination, CanalConnector connector, BinLogService binLogService) {
        super(destination, connector, binLogService);
    }
}
