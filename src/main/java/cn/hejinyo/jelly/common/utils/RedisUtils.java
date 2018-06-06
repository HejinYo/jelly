package cn.hejinyo.jelly.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
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
    private ListOperations<String, Object> listOperations;
    @Autowired
    private HashOperations<String, String, Object> hashOperations;
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


    /**
     * RPOPLPUSH source destination 删除列表中的最后一个元素，将其追加到另一个列表
     */
    public Object rpoplpush(String sourceKey, String destinationKey) {
        return listOperations.rightPopAndLeftPush(sourceKey, destinationKey);
    }

    /**
     * LINDEX key index 获取一个元素，通过其索引列表
     */
    public Object lindex(String key, long index) {
        return listOperations.index(key, index);
    }

    /**
     * LLEN key 获得队列(List)的长度
     */
    public Object llen(String key) {
        return listOperations.size(key);
    }

    /**
     * LPOP key 从队列的左边出队一个元素
     */
    public Object lpop(String key) {
        return listOperations.leftPop(key);
    }

    /**
     * LPUSH key value [value ...] 从队列的左边入队一个元素
     */
    public Long lpush(String key, Object value) {
        return listOperations.leftPush(key, value);
    }

    /**
     * LPUSH key value [value ...] 从队列的左边入队多个元素
     */
    public Long lpush(String key, Object... values) {
        return listOperations.leftPushAll(key, values);
    }

    /**
     * LPUSH key value [value ...] 从队列的左边入队一个集合
     */
    public Long lpush(String key, Collection<Object> values) {
        return listOperations.leftPushAll(key, values);
    }

    /**
     * LPUSHX key value 当队列存在时，从队到左边入队一个元素
     */
    public Long lpushx(String key, Object value) {
        return listOperations.leftPushIfPresent(key, value);
    }

    /**
     * LRANGE key start stop 从列表中获取指定返回的元素
     * list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推。
     * 偏移量也可以是负数，表示偏移量是从list尾部开始计数。 例如， -1 表示列表的最后一个元素，-2 是倒数第二个，以此类推。
     */
    public List<Object> lrange(String key, long start, long end) {
        return listOperations.range(key, start, end);
    }

    /**
     * LREM key count value 从列表中删除元素
     * count > 0: 从头往尾移除值为 value 的元素。
     * count < 0: 从尾往头移除值为 value 的元素。
     * count = 0: 移除所有值为 value 的元素。
     */
    public Long lrem(String key, long count, Object value) {
        return listOperations.remove(key, count, value);
    }

    /**
     * LSET key index value 设置队列里面一个元素的值
     */
    public void lset(String key, long index, Object value) {
        listOperations.set(key, index, value);
    }

    /**
     * LTRIM key start stop 修剪到指定范围内的清单
     * 如果 start 超过列表尾部，或者 start > end，结果会是列表变成空表（即该 key 会被移除）。
     * 如果 end 超过列表尾部，Redis 会将其当作列表的最后一个元素。
     */
    public void ltrim(String key, long start, long end) {
        listOperations.trim(key, start, end);
    }

    /**
     * RPOP key 从队列的右边出队一个元素
     */
    public Object rpop(String key) {
        return listOperations.rightPop(key);
    }

    /**
     * RPUSH key value [value ...] 从队列的右边入队一个元素
     */
    public Object rpush(String key, Object value) {
        return listOperations.rightPush(key, value);
    }

    /**
     * RPUSH key value [value ...] 从队列的右边入队多個元素
     */
    public Long rpush(String key, Object... values) {
        return listOperations.rightPushAll(key, values);
    }

    /**
     * RPUSH key value [value ...] 从队列的右边入队一个集合
     */
    public Long rpush(String key, Collection<Object> values) {
        return listOperations.rightPushAll(key, values);
    }

    /**
     * LPUSHX key value 当队列存在时，从队到左边入队一个元素
     */
    public Long rpushx(String key, Object value) {
        return listOperations.rightPushIfPresent(key, value);
    }

    /**
     * HDEL key field [field ...] 删除一个或多个Hash的field
     */

    /**
     * HEXISTS key field 判断field是否存在于hash中
     */

    /**
     * HGET key field 获取hash中field的值
     */

    /**
     * HGETALL key 从hash中读取全部的域和值
     */

    /**
     * HINCRBY key field increment 将hash中指定域的值增加给定的数字
     */

    /**
     * HINCRBYFLOAT key field increment 将hash中指定域的值增加给定的浮点数
     */

    /**
     * HKEYS key 获取hash的所有字段
     */

    /**
     * HLEN key 获取hash里所有字段的数量
     */

    /**
     * HMGET key field [field ...] 获取hash里面指定字段的值
     */

    /**
     * HMSET key field value [field value ...] 设置hash字段值
     */

    /**
     * HSET key field value 设置hash里面一个字段的值
     */

    /**
     * HSETNX key field value 设置hash的一个字段，只有当这个字段不存在时有效
     */

    /**
     * HSTRLEN key field 获取hash里面指定field的长度
     */

    /**
     * HVALS key  获得hash的所有值
     */

    /**
     * HSCAN key cursor [MATCH pattern] [COUNT count] 迭代hash里面的元素
     */


}
