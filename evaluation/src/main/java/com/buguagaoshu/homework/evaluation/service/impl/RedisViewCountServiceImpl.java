package com.buguagaoshu.homework.evaluation.service.impl;

import com.buguagaoshu.homework.common.config.CustomConstant;
import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import com.buguagaoshu.homework.evaluation.service.ArticleService;
import com.buguagaoshu.homework.evaluation.service.ViewCountService;
import com.buguagaoshu.homework.evaluation.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-04-12 16:22
 */
@Service
@Slf4j
public class RedisViewCountServiceImpl implements ViewCountService {
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ArticleService articleService;

    @Autowired
    public RedisViewCountServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * Key： CustomConstant.VIEW_COUNT_KEY + articleId
     */
    @Override
    public void viewCountAdd(HttpServletRequest request, Long articleId) {
        String key = CustomConstant.VIEW_COUNT_KEY + articleId;
        String ip = IpUtil.getIpAddr(request);
        redisTemplate.opsForSet().add(key, ip);
    }

    @Override
    public void viewCountAdd(HttpServletRequest request, List<ArticleEntity> articleIds) {
        if (articleIds == null) {
            return;
        }
        if (articleIds.size() == 0) {
            return;
        }
        RedisSerializer keyS = redisTemplate.getKeySerializer();
        RedisSerializer valueS = redisTemplate.getValueSerializer();
        String ip = IpUtil.getIpAddr(request);
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            articleIds
                    .stream()
                    .forEach(articleId ->
                            connection.sAdd(keyS.serialize(CustomConstant.VIEW_COUNT_KEY + articleId.getId()), valueS.serialize(ip))
                    );
            return null;
        });
    }

    @Override
    public long getViewCount(Long articleId) {
        String key = CustomConstant.VIEW_COUNT_KEY + articleId;
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Map<Long, Long> multiGetViewCount(List<ArticleEntity> ids) {

        if (ids == null) {
            return new HashMap<>(0);
        }
        if (ids.size() == 0) {
            return new HashMap<>(0);
        }
        HashMap<Long, Long> hashMap = new HashMap<>(ids.size());
        /**
         * TODO 优化查询方法
         * */
        ids.forEach(a -> {
            hashMap.put(a.getId(), getViewCount(a.getId()));
        });

        return hashMap;
    }

    @Override
    public void synchronizedViewData() {

        int length = CustomConstant.VIEW_COUNT_KEY.length();
        int count = 1;
        AtomicLong dataNumber = new AtomicLong(0L);
        while (true) {
            log.info("浏览量同步开始第 {} 次循环！", count);
            if (findKey(dataNumber, length) == null) {
                break;
            }
            count++;
        }
        log.info("浏览量同步结束!!!");
    }


    private Set<Object> findKey(AtomicLong dataNumber, int length) {
        return redisTemplate.execute(new RedisCallback<Set<Object>>() {
            @Override
            public Set<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                Map<Long, Long> count = new HashMap<>(100);
                // 一次返回1000个
                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(CustomConstant.VIEW_COUNT_KEY + "*").count(5).build());
                if (!cursor.hasNext()) {
                    log.info("此次共完成 {} 个数据同步！！！浏览量同步结束！！！", dataNumber.get());
                    return null;
                }
                while (cursor.hasNext()) {
                    dataNumber.addAndGet(1L);
                    byte[] c = cursor.next();
                    String key = new String(c);
                    long id = Long.parseLong(key.substring(length));
                    count.put(id, getViewCount(id));
                    // 移除KEY
                    connection.del(c);
                }
                // 查询
                List<ArticleEntity> articleEntities = articleService.listByIds(count.keySet());
                articleEntities.forEach(a -> {
                    a.setViewCount(count.get(a.getId()) + a.getViewCount());
                });
                // 更新
                articleService.updateBatchById(articleEntities);
                return new HashSet<>();
            }
        });
    }
}
