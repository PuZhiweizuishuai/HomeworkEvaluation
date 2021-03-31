package com.buguagaoshu.homework.binlogcanal.service;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.buguagaoshu.homework.binlogcanal.config.AlibabaCanalProperties;
import com.buguagaoshu.homework.binlogcanal.utils.CreateSearchObjectUtils;
import com.buguagaoshu.homework.common.enums.TableEventType;
import com.buguagaoshu.homework.common.search.SearchTableNameConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 13:43
 */
@Service
@Slf4j
public class BinLogService {
    private final AlibabaCanalProperties alibabaCanalProperties;

    private final CreateSearchObjectUtils createSearchObjectUtils;

    private final KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    public BinLogService(AlibabaCanalProperties alibabaCanalProperties, CreateSearchObjectUtils createSearchObjectUtils, KafkaTemplate<String, String> kafkaTemplate) {
        this.alibabaCanalProperties = alibabaCanalProperties;
        this.createSearchObjectUtils = createSearchObjectUtils;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            // 判断是否属于目标数据库
            if (entry.getHeader().getSchemaName().equals(alibabaCanalProperties.getDatabase())) {
                setCol(entry);
            }
        }
    }

    public void setCol(CanalEntry.Entry entry) {
        if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
            CanalEntry.RowChange rowChange = null;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
            }

            CanalEntry.EventType eventType = rowChange.getEventType();
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                judgeTable(entry, eventType, rowData);
            }
        }
    }


    public void judgeTable(CanalEntry.Entry entry, CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        if (SearchTableNameConstant.hasTable(entry.getHeader().getTableName())) {
            String json = "";
            if (eventType == CanalEntry.EventType.DELETE) {
                json = createSearchObjectUtils.objectToJson(rowData.getBeforeColumnsList(), entry.getHeader().getTableName(), TableEventType.DELETE);
            } else if (eventType == CanalEntry.EventType.INSERT) {
                json = createSearchObjectUtils.objectToJson(rowData.getAfterColumnsList(), entry.getHeader().getTableName(), TableEventType.INSERT);
            } else if (eventType == CanalEntry.EventType.UPDATE) {
                json = createSearchObjectUtils.objectToJson(rowData.getAfterColumnsList(), entry.getHeader().getTableName(), TableEventType.UPDATE);
            }
            if (json.equals("")) {
                log.info("数据库Binlog数据序列化失败！");
                return;
            }
            // 发送数据
            kafkaTemplate.send("searchDataSynchronize", json);
        }
    }
}
