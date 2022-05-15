package com.tangcheng.face_search.common.redis;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    /** 缓存的有效时长 **/
    long TIME_ONE_SECOND = 1; // 1秒
    long TIME_ONE_MINUTE = 60 * TIME_ONE_SECOND; // 1分
    long TIME_ONE_HOUR = 60 * TIME_ONE_MINUTE; // 1小时
    long TIME_ONE_DAY = 24 * TIME_ONE_HOUR; // 1天
    long TIME_ONE_MONTH = 30 * TIME_ONE_DAY; // 1个月

    /**
     * 获取缓存剩余时间
     * @param keys
     * @return 秒
     */
    long getExpire(String... keys);

    /**
     * 缓存数据 并 设置有效期
     * @param value
     * @param exp
     * @param unit
     * @param keys
     * @param <V>
     * @return
     */
    <V> boolean setByKey(V value, long exp, TimeUnit unit, String... keys);

    /**
     * 缓存数据 并 设置有效期（默认时间单位为秒）
     * @param value
     * @param exp
     * @param keys
     * @param <V>
     * @return
     */
    <V> boolean setByKey(V value, long exp, String... keys);

    /**
     * 缓存数据（默认存储一个月）
     * @param value
     * @param keys
     * @param <V>
     * @return
     */
    <V> boolean setByKey(V value, String... keys);

    /**
     * incr key
     * 原子递增
     * @param key
     * @param value
     * @return
     */
    long increment(String key, long value);

    /**
     * incr key
     * 原子递增
     * @param key
     * @param value
     * @param exp
     * @return
     */
    long increment(String key, long value, long exp);

    /**
     * rpush = right push (rpush key value)
     * 添加元素到List集合的尾部（默认存储时间单位为秒）
     * @param key
     * @param value 一个元素 或 一个List集合
     * @param exp
     * @param <V>
     * @return
     */
    <V> boolean rpush(String key, V value, long exp);

    /**
     * lpush = left push (lpush key value)
     * 添加元素到List集合的头部（默认存储时间单位为秒）
     * @param key
     * @param value 一个元素 或 一个List集合
     * @param exp
     * @param <V>
     * @return
     */
    <V> boolean lpush(String key, V value, long exp);

    /**
     * lrange = list range (lrange key 0 -1 即获取全部列表数据)
     * 获取List集合数据
     * @param key
     * @param <V>
     * @return
     */
    <V> V lrange(String key);

    /**
     * sadd key value
     * 添加元素到Set集合（默认存储时间单位为秒）
     * @param key
     * @param value 一个元素 或 一个Set集合
     * @param exp
     * @param <V>
     * @return
     */
    <V> boolean sadd(String key, V value, long exp);

    /**
     * smembers key
     * 获取Set集合数据
     * @param key
     * @param <V>
     * @return
     */
    <V> V smembers(String key);

    /**
     * zadd key score value (score为有序索引序列)
     * 添加元素到ZSet集合(有序)（默认存储时间单位为秒）
     * @param key
     * @param value 一个元素 或 一个Set集合
     * @param exp
     * @param <V>
     * @return
     */
    <V> boolean zadd(String key, V value, long exp);

    /**
     * zrange key 0 -1
     * 获取ZSet集合数据
     * @param key
     * @param <V>
     * @return
     */
    <V> V zrange(String key);

    /**
     * 获取Redis缓存数据
     * @param keys
     * @param <V>
     * @return
     */
    <V> V getByKey(String... keys);

    /**
     * 删除Redis缓存数据
     * @param keys
     * @return
     */
    boolean delByKey(String... keys);

    /**
     * 通过 Redis 发布订阅消息
     * @param channel
     * @param message
     */
    void convertAndSend(String channel, Object message);

}
