package com.buguagaoshu.homework.danmaku.service;

import com.buguagaoshu.homework.danmaku.damain.Danmaku;

import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-07 22:10
 */
public interface DanmakuService {

    boolean save(Danmaku danmaku);

    boolean delete(Danmaku danmaku);

    List<List<Object>> danmakuList(Long coursewareId);
}
