package com.buguagaoshu.homework.binlogcanal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.buguagaoshu.homework.binlogcanal.service.BinLogService;
import org.slf4j.MDC;
import org.springframework.util.Assert;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-02-27 23:41
 */
public class AbstractCanalClient extends BaseCanalClient {
    private final BinLogService binLogService;

    public AbstractCanalClient(String destination, BinLogService binLogService) {
        this(destination, null, binLogService);
    }

    public AbstractCanalClient(String destination, CanalConnector connector, BinLogService binLogService) {
        this.destination = destination;
        this.connector = connector;
        this.binLogService = binLogService;
    }

    public void start() {
        Assert.notNull(connector, "connector is null");
        thread = new Thread(this::process);

        thread.setUncaughtExceptionHandler(handler);
        running = true;
        thread.start();
    }

    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }

        MDC.remove("destination");
    }

    protected void process() {
        int batchSize = 5 * 1024;
        while (running) {
            try {
                MDC.put("destination", destination);
                connector.connect();
                connector.subscribe();
                while (running) {
                    // 获取指定数量的数据
                    Message message = connector.getWithoutAck(batchSize);
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) { }
                    } else {

                        printSummary(message, batchId, size);
                        // 推送到Kafka, 让ES同步数据库数据
                        binLogService.sendMessage(message.getEntries());
                    }
                    if (batchId != -1) {
                        // 提交确认
                        connector.ack(batchId);
                    }
                }
            } catch (Throwable e) {
                logger.error("process error!", e);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e1) {
                    // ignore
                }
                // 处理失败, 回滚数据
                connector.rollback();
            } finally {
                connector.disconnect();
                MDC.remove("destination");
            }
        }
    }
}
