package cn.hejinyo.jelly.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-07-17 21:12
 */
@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ValueOperations<String, String> valueOperations;
    @Autowired
    private HashOperations<String, String, Object> hashOperations;
    @Autowired
    private ListOperations<String, Object> listOperations;
    @Autowired
    private SetOperations<String, Object> setOperations;
    @Autowired
    private ZSetOperations<String, Object> zSetOperations;

    /**
     * 默认过期时长，单位：秒
     */
    private final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**
     * 不设置过期时长
     */
    private final static long NOT_EXPIRE = -1;
    /**
     * key不存在返回的过期時常
     */
    private final static long NO_KEY_EXPIRE = -2;

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return JsonUtil.toJson(object);
    }

    /**
     * 删除一个key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 清除所有前缀为key
     */
    public String cleanKey(String key) {
        Set<String> keys = redisTemplate.keys(key);
        for (String k : keys) {
            redisTemplate.delete(k);
        }
        return keys.toString();
    }

    /**
     * SET key value [EX seconds] [PX milliseconds] [NX|XX] 设置一个key的value值,有效期永久
     * EX seconds – 设置键key的过期时间，单位时秒
     * PX milliseconds – 设置键key的过期时间，单位时毫秒
     * NX – 只有键key不存在的时候才会设置key的值
     * XX – 只有键key存在的时候才会设置key的值
     */
    public void set(String key, Object value) {
        valueOperations.set(key, toJson(value));
    }

    /**
     * SETNX key value 设置的一个关键的价值，只有当该键不存在
     */
    public void setnx(String key, Object value) {
        valueOperations.setIfAbsent(key, toJson(value));
    }

    /**
     * SETEX key seconds value 设置key-value并设置过期时间（单位：秒）
     */
    public void setex(String key, Object value, long expire) {
        valueOperations.set(key, toJson(value), expire, TimeUnit.SECONDS);
    }

    /**
     * 设置 key value,不改变原来有效期
     */
    public void setHold(String key, Object value) {
        // Key不在，超时返回-2
        Long expire = redisTemplate.getExpire(key);
        valueOperations.set(key, toJson(value), expire != NO_KEY_EXPIRE ? expire : DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 获取一个值
     */
    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    /**
     * 获取一个值，并设置有效时间
     */
    public String get(String key, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    /**
     * 获取一个值，并转换成指定对象
     */
    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    /**
     * 获取一个值，并转换成指定对象，并设置有效时间
     */
    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : JsonUtil.fromJson(value, clazz);
    }

    /**
     * APPEND key value
     * 追加一个值到key上
     */
    public Integer append(String key, Object value) {
        return valueOperations.append(key, toJson(value));
    }

    /**
     * DECR key 整数原子减1
     */
    public Long decr(String key) {
        return valueOperations.increment(key, -1);
    }

    /**
     * DECR key 整数原子减指定整数
     */
    public Long decrby(String key, Long decrement) {
        return valueOperations.increment(key, -decrement);
    }

    /**
     * INCR key 执行原子加1操作
     */
    public Long incr(String key) {
        return valueOperations.increment(key, 1);
    }

    /**
     * INCRBY key 整数原子加指定整数
     */
    public Long incrby(String key, Long decrement) {
        return valueOperations.increment(key, decrement);
    }

    /**
     * GETSET key value 设置一个key的value，并获取设置前的值
     */
    public String getAndSet(String key, Object value) {
        return valueOperations.getAndSet(key, toJson(value));
    }

    /**
     * BLPOP key [key ...] timeout
     * 删除，并获得该列表中的第一元素，或阻塞，直到有一个可用
     */
    public Object blpop(String key, long timeout) {
        return listOperations.leftPop(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * BRPOP key [key ...] timeout 删除，并获得该列表中的最后一个元素，或阻塞，直到有一个可用
     */
    public Object brpop(String key, long timeout) {
        return listOperations.rightPop(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * BRPOPLPUSH source destination timeout 弹出一个列表的值，将它推到另一个列表，并返回它;或阻塞，直到有一个可用
     */
    public Object brpoplpush(String source, String destination, long timeout) {
        if (timeout == 0) {
            // timeout 为 0 能用于无限期阻塞客户端。
            return listOperations.rightPopAndLeftPush(source, destination);
        }
        return listOperations.rightPopAndLeftPush(source, destination, timeout, TimeUnit.SECONDS);
    }

    public Object lpop(String key) {
        return listOperations.leftPop(key);
    }

}
