package com.buguagaoshu.homework.binlogcanal.client;

import com.alibaba.otter.canal.client.CanalConnector;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-02-27 23:42
 * 连接到 Alibaba Canal
 */
public class ClusterCanalClient extends AbstractCanalClient {
    public ClusterCanalClient(String destination) {
        super(destination);
    }

    public ClusterCanalClient(String destination, CanalConnector connector) {
        super(destination, connector);
    }
}
