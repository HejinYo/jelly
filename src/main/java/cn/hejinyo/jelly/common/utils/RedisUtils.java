package cn.hejinyo.jelly.common.utils;

import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author : HejinYo
 * @date : 2017/8/19 17:31
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
     * DEL key [key ...]
     * 删除指定的key（一个或多个）
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 清除所有前缀为key
     */
    public void cleanKey(String key) {
        Set<String> keys = redisTemplate.keys(key);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * EXISTS key [key ...]
     * 查询一个key是否存在
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * PEXPIRE key milliseconds
     * 设置key的有效时间以毫秒为单位
     */
    public Boolean pExpire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * EXPIRE key seconds
     * 设置一个key的过期的秒数
     */
    public Boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * EXPIREAT key timestamp
     * 设置一个UNIX时间戳的过期时间
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * KEYS pattern
     * 查找所有匹配给定的模式的键
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * PERSIST key
     * 移除key的过期时间
     */
    public Boolean expireAt(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * TTL key
     * 获取key的有效时间（单位：秒）
     */
    public Long ttl(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * PTTL key
     * 获取key的有效毫秒数
     */
    public Long pttl(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * RANDOMKEY
     * 返回一个随机的key
     */
    public String randomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * RENAME key newkey
     * 将一个key重命名
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * RENAMENX key newkey
     * 重命名一个key,新的key必须是不存在的key
     */
    public void renameNX(String oldKey, String newKey) {
        redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * TYPE key
     * 获取key的存储类型
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
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
    public void setNX(String key, Object value) {
        valueOperations.setIfAbsent(key, toJson(value));
    }

    /**
     * SETEX key seconds value 设置key-value并设置过期时间（单位：秒）
     */
    public void setEX(String key, Object value, long expire) {
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
    public Long decrBy(String key, Long decrement) {
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
    public Long incrBy(String key, Long decrement) {
        return valueOperations.increment(key, decrement);
    }

    /**
     * GETSET key value 设置一个key的value，并获取设置前的值
     */
    public String getSet(String key, Object value) {
        return valueOperations.getAndSet(key, toJson(value));
    }

    /**
     * BLPOP key [key ...] timeout
     * 删除，并获得该列表中的第一元素，或阻塞，直到有一个可用
     */
    public Object bLPop(String key, long timeout) {
        return listOperations.leftPop(key, timeout, TimeUnit.SECONDS);
    }

    public <T> T bLPop(String key, long timeout, Class<T> clazz) {
        Object value = bLPop(key, timeout);
        return value == null ? null : JsonUtil.fromJson(String.valueOf(value), clazz);
    }

    /**
     * BRPOP key [key ...] timeout 删除，并获得该列表中的最后一个元素，或阻塞，直到有一个可用
     */
    public Object bRPop(String key, long timeout) {
        return listOperations.rightPop(key, timeout, TimeUnit.SECONDS);
    }

    public <T> T bRPop(String key, long timeout, Class<T> clazz) {
        Object value = bRPop(key, timeout);
        return value == null ? null : JsonUtil.fromJson(String.valueOf(value), clazz);
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

    public <T> T bRPop(String source, String destination, long timeout, Class<T> clazz) {
        Object value = brpoplpush(source, destination, timeout);
        return value == null ? null : JsonUtil.fromJson(String.valueOf(value), clazz);
    }

    /**
     * RPOPLPUSH source destination 删除列表中的最后一个元素，将其追加到另一个列表
     */
    public Object rpoplpush(String sourceKey, String destinationKey) {
        return listOperations.rightPopAndLeftPush(sourceKey, destinationKey);
    }

    public <T> T rpoplpush(String sourceKey, String destinationKey, Class<T> clazz) {
        Object value = rpoplpush(sourceKey, destinationKey);
        return value == null ? null : JsonUtil.fromJson(String.valueOf(value), clazz);
    }

    /**
     * LINDEX key index 获取一个元素，通过其索引列表
     */
    public Object lindex(String key, long index) {
        return listOperations.index(key, index);
    }

    public <T> T lindex(String key, long index, Class<T> clazz) {
        Object value = lindex(key, index);
        return value == null ? null : JsonUtil.fromJson(String.valueOf(value), clazz);
    }

    /**
     * LLEN key 获得队列(List)的长度
     */
    public Long llen(String key) {
        return listOperations.size(key);
    }

    /**
     * LPOP key 从队列的左边出队一个元素
     */
    public Object lpop(String key) {
        return listOperations.leftPop(key);
    }

    public <T> T lpop(String key, Class<T> clazz) {
        Object value = lpop(key);
        return value == null ? null : JsonUtil.fromJson(String.valueOf(value), clazz);
    }

    /**
     * LPUSH key value [value ...] 从队列的左边入队一个元素
     */
    public Long lpush(String key, Object value) {
        return listOperations.leftPush(key, toJson(value));
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
        return listOperations.leftPushAll(key, values.stream().map(this::toJson));
    }

    /**
     * LPUSHX key value 当队列存在时，从队到左边入队一个元素
     */
    public Long lpushx(String key, Object value) {
        return listOperations.leftPushIfPresent(key, toJson(value));
    }

    /**
     * LRANGE key start stop 从列表中获取指定返回的元素
     * list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推。
     * 偏移量也可以是负数，表示偏移量是从list尾部开始计数。 例如， -1 表示列表的最后一个元素，-2 是倒数第二个，以此类推。
     */
    public List<Object> lrange(String key, long start, long end) {
        return listOperations.range(key, start, end);
    }

    public <T> List<T> lrange(String key, long start, long end, Class<T> clazz) {
        List<Object> value = lrange(key, start, end);
        return value == null ? null : JsonUtil.fromJson(String.valueOf(value), new TypeToken<List<T>>() {
        }.getType());
        //return value == null ? null : value.stream().map(o -> (T) JsonUtil.fromJson(String.valueOf(o), clazz)).collect(Collectors.toList());
    }

    /**
     * LREM key count value 从列表中删除元素
     * count > 0: 从头往尾移除值为 value 的元素。
     * count < 0: 从尾往头移除值为 value 的元素。
     * count = 0: 移除所有值为 value 的元素。
     */
    public Long lrem(String key, long count, Object value) {
        return listOperations.remove(key, count, toJson(value));
    }

    /**
     * LSET key index value 设置队列里面一个元素的值
     */
    public void lset(String key, long index, Object value) {
        listOperations.set(key, index, toJson(value));
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

    public <T> T rpop(String key, Class<T> clazz) {
        Optional<Object> vv = Optional.ofNullable(rpop(key));
        return vv.map(o -> (T) JsonUtil.fromJson(String.valueOf(o), clazz)).orElse(null);
       /* Object value = rpop(key);
        return value == null ? null : JsonUtil.fromJson(String.valueOf(value), clazz);*/
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
     * http://redis.cn/commands.html#hash
     * HSTRLEN key field 获取hash里面指定field的长度
     * HSCAN key cursor [MATCH pattern] [COUNT count] 迭代hash里面的元素
     */

    /**
     * HDEL key field [field ...] 删除一个或多个Hash的field
     */
    public Long hdel(String key, String field) {
        return hashOperations.delete(key, field);
    }

    /**
     * HEXISTS key field 判断field是否存在于hash中
     */
    public Boolean hexists(String key, String field) {
        return hashOperations.hasKey(key, field);
    }

    /**
     * HGET key field 获取hash中field的值
     */
    public Object hget(String key, String field) {
        return hashOperations.get(key, field);
    }

    /**
     * HGETALL key 从hash中读取全部的域和值
     */
    public Map<String, Object> hgetall(String key) {
        return hashOperations.entries(key);
    }

    /**
     * HINCRBY key field increment 将hash中指定域的值增加给定的数字
     */
    public Long hincrby(String key, String field) {
        return hashOperations.increment(key, field, 1L);
    }

    /**
     * HINCRBYFLOAT key field increment 将hash中指定域的值增加给定的浮点数
     */
    public Long hincrbyfloat(String key, String field) {
        return hashOperations.increment(key, field, 1L);
    }

    /**
     * HKEYS key 获取hash的所有字段
     */
    public Set<String> hkeys(String key) {
        return hashOperations.keys(key);
    }

    /**
     * HLEN key 获取hash里所有字段的数量
     */
    public Long hlen(String key) {
        return hashOperations.size(key);
    }

    /**
     * HMGET key field [field ...] 获取hash里面指定字段的值
     */
    public List<Object> hmget(String key, Collection<String> hashKeys) {
        return hashOperations.multiGet(key, hashKeys);
    }

    /**
     * HMSET key field value [field value ...] 设置hash字段值
     */
    public void hmset(String key, Map<String, Object> m) {
        hashOperations.putAll(key, m);
    }

    /**
     * HSET key field value 设置hash里面一个字段的值
     */
    public void hset(String key, String hashKey, Object value) {
        hashOperations.put(key, hashKey, value);
    }

    /**
     * HSETNX key field value 设置hash的一个字段，只有当这个字段不存在时有效
     */
    public Boolean hsetnx(String key, String hashKey, Object value) {
        return hashOperations.putIfAbsent(key, hashKey, value);
    }

    /**
     * HVALS key  获得hash的所有值
     */
    public List<Object> hvals(String key) {
        return hashOperations.values(key);
    }

    /*
     *http://redis.cn/commands.html#set
     * SSCAN key cursor [MATCH pattern] [COUNT count]
     * 迭代set里面的元素
     */

    /**
     * SADD key member [member ...]
     * 添加一个或者多个元素到集合(set)里
     **/
    public Long sadd(String key, Object... values) {
        return setOperations.add(key, values);
    }

    /**
     * SCARD key
     * 获取集合里面的元素数量
     */
    public Long scard(String key) {
        return setOperations.size(key);
    }

    /**
     * SDIFF key [key ...]
     * 获得队列不存在的元素
     **/
    public Set<Object> sdiff(String key, String otherKey) {
        return setOperations.difference(key, otherKey);
    }

    /**
     * SDIFFSTORE destination key [key ...]
     * 获得队列不存在的元素，并存储在一个关键的结果集
     **/
    public Long sdiffstore(String key, String otherKey, String destKey) {
        return setOperations.differenceAndStore(key, otherKey, destKey);
    }

    /**
     * SINTER key [key ...]
     * 获得两个集合的交集
     **/
    public Set<Object> sinter(String key, String otherKey) {
        return setOperations.intersect(key, otherKey);
    }

    /**
     * SINTERSTORE destination key [key ...]
     * 获得两个集合的交集，并存储在一个关键的结果集
     **/
    public Long sinterstore(String key, String otherKey, String destKey) {
        return setOperations.intersectAndStore(key, otherKey, destKey);
    }

    /**
     * SISMEMBER key member
     * 确定一个给定的值是一个集合的成员
     **/
    public Boolean sismember(String key, Object o) {
        return setOperations.isMember(key, o);
    }

    /**
     * SMEMBERS key
     * 获取集合里面的所有元素
     **/
    public Set<Object> smembers(String key) {
        return setOperations.members(key);
    }

    /**
     * SMOVE source destination member
     * 移动集合里面的一个元素到另一个集合
     **/
    public Boolean smove(String key, String value, String destKey) {
        return setOperations.move(key, value, destKey);
    }

    /**
     * SPOP key [count]
     * 删除并获取一个集合里面的元素
     **/
    public Object spop(String key) {
        return setOperations.pop(key);
    }

    /**
     * SRANDMEMBER key
     * 从集合里面随机获取一个元素
     **/
    public Object srandmember(String key) {
        return setOperations.randomMember(key);
    }

    /**
     * SRANDMEMBER key [count]
     * 从集合里面随机获取多个元素
     **/
    public List<Object> srandmember(String key, long count) {
        return setOperations.randomMembers(key, count);
    }

    /**
     * SREM key member [member ...]
     * 从集合里删除一个或多个元素
     **/
    public Long srem(String key, Object... values) {
        return setOperations.remove(key, values);
    }

    /**
     * SUNION key [key ...]
     * 添加多个set元素
     **/
    public Set<Object> sunion(String key, String otherKey) {
        return setOperations.union(key, otherKey);
    }

    /**
     * SUNIONSTORE destination key [key ...]
     * 合并set元素，并将结果存入新的set里面
     **/
    public Long sunionstore(String key, String otherKey, String destKey) {
        return setOperations.unionAndStore(key, otherKey, destKey);
    }

}
