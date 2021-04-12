package com.buguagaoshu.homework.evaluation.repository.impl;

import com.buguagaoshu.homework.common.config.CustomConstant;
import com.buguagaoshu.homework.evaluation.repository.VerifyCodeRepository;
import com.buguagaoshu.homework.evaluation.service.impl.DigitsVerifyCodeServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2019-11-26 17:07
 * 在内存中保存需要验证的验证码
 * TODO 放到 redis session 里
 */
@Repository
public class RedisVerifyCodeRepositoryImpl implements VerifyCodeRepository {

    private static final int DEFAULT_VERIFY_CODE_LENGTH = 4;



    private final RedisTemplate<String, String> redisTemplate;

    public RedisVerifyCodeRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String key, String code) {
        redisTemplate.opsForValue().set(key, code, DigitsVerifyCodeServiceImpl.VERIFY_CODE_EXPIRE_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Override
    public void save(String key, String code, Long time) {
        redisTemplate.opsForValue().set(CustomConstant.VERIFY_HASH_KEY + key, code, time, TimeUnit.MINUTES);
    }

    @Override
    public String find(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }
}
