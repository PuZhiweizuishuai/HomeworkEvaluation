package com.buguagaoshu.homework.danmaku.util;

import com.buguagaoshu.homework.danmaku.damain.Danmaku;


import java.util.LinkedList;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-06 23:51
 */
public class BuildBackInfo {
    public List<Object> createDanmaku(Danmaku danmaku) {
        List<Object> d = new LinkedList<>();
//        d.getTime(),
//                d.getType(),
//                d.getColorDec(),
//                d.getColor(),
//                d.getText()
        d.add(danmaku.getTime());
        d.add(danmaku.getType());
        d.add(danmaku.getColorDec());
        d.add(danmaku.getColor());
        d.add(danmaku.getText());
        // 以下两个字段在前端用不到，仅仅用作后期查找
        // 发送人信息
        d.add(danmaku.getAuthor());
        // 唯一表示
        d.add(danmaku.getId());
        return d;
    }
}
