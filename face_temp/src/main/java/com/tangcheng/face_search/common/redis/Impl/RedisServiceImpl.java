package com.tangcheng.face_search.common.redis.Impl;

import com.alibaba.fastjson.JSON;
import com.tangcheng.face_search.common.redis.RedisService;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 拼接缓存数据的key
     * @param keys
     * @return
     */
    private String getMemKey(String... keys){
        if(keys == null || keys.length == 0){
            return null;
        }
        if(keys.length == 1){
            return keys[0];
        }

        StringBuffer memKey = new StringBuffer();
        Arrays.stream(keys).forEach(key -> {
            memKey.append(":");
            memKey.append(key);
        });

        return memKey.toString().replaceFirst(":", "");
    }

    /**
     * 设置缓存有效期
     * @param key
     * @param exp 默认一个月
     * @param unit 默认为秒
     * @return
     */
    private boolean expireByKey(String key, long exp, TimeUnit unit){
        if (exp == 0) {
            exp = RedisService.TIME_ONE_MONTH;
        }
        if (unit == null) {
            unit = TimeUnit.SECONDS;
        }

        return redisTemplate.expire(key, exp, unit);
    }

    @Override
    public long getExpire(String... keys) {
        String memKey = this.getMemKey(keys);
        Long expire = redisTemplate.getExpire(memKey);

        log.info("获取缓存数据剩余有效期：key-{}, value-{}", memKey, expire);

        return expire == null ? 0 : expire;
    }

    @Override
    public <V> boolean setByKey(V value, long exp, TimeUnit unit, String... keys) {
        String memKey = this.getMemKey(keys);
        redisTemplate.opsForValue().set(memKey, value);
        return this.expireByKey(memKey, exp, unit);
    }

    @Override
    public <V> boolean setByKey(V value, long exp, String... keys) {
        return setByKey(value, exp, TimeUnit.SECONDS, keys);
    }

    @Override
    public <V> boolean setByKey(V value, String... keys) {
        return setByKey(value, 0, keys);
    }

    @Override
    public long increment(String key, long value) {
        // 永不过期
        return redisTemplate.opsForValue().increment(key, value);
    }

    @Override
    public long increment(String key, long value, long exp) {
        long afterIncrementValue = redisTemplate.opsForValue().increment(key, value);
        this.expireByKey(key, exp, TimeUnit.SECONDS);
        return afterIncrementValue;
    }

    @Override
    public <V> boolean rpush(String key, V value, long exp) {
        redisTemplate.opsForList().rightPush(key, value);
        return this.expireByKey(key, exp, TimeUnit.SECONDS);
    }

    @Override
    public <V> boolean lpush(String key, V value, long exp) {
        redisTemplate.opsForList().leftPush(key, value);
        return this.expireByKey(key, exp, TimeUnit.SECONDS);
    }

    @Override
    public <V> V lrange(String key) {
        log.debug("获取缓存数据：key-{}", key);
        return (V) redisTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public <V> boolean sadd(String key, V value, long exp) {
        redisTemplate.opsForSet().add(key, value);
        return this.expireByKey(key, exp, TimeUnit.SECONDS);
    }

    @Override
    public <V> V smembers(String key) {
        log.debug("获取缓存数据：key-{}", key);
        return (V) redisTemplate.opsForSet().members(key);
    }

    @Override
    public <V> boolean zadd(String key, V value, long exp) {
        redisTemplate.opsForZSet().add(key, value, Math.random());
        return this.expireByKey(key, exp, TimeUnit.SECONDS);
    }

    @Override
    public <V> V zrange(String key) {
        log.debug("获取缓存数据：key-{}", key);
        return (V) redisTemplate.opsForZSet().range(key, 0, -1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getByKey(String... keys) {
        String memKey = this.getMemKey(keys);
        V value = (V) redisTemplate.opsForValue().get(memKey);

        log.info("获取缓存数据：key-{}, value-{}", memKey, JSON.toJSONString(value));

        return value;
    }

    @Override
    public boolean delByKey(String... keys) {
        return redisTemplate.opsForValue().getOperations().delete(this.getMemKey(keys));
    }

    @Override
    public void convertAndSend(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }


}
