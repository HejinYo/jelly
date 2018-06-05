package cn.hejinyo.jelly.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置，开启注解式缓存
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/17 23:22
 */
@Configuration
@EnableCaching
public class RedisConfig {
    /**
     * 默认缓存时长
     */
    private static final long DEFAULTEXPIRETIME = 600;

    /**
     * 缓存管理器
     *
     * @ Cacheable(value = "user", key = "#root.targetClass.getCanonicalName()", unless = "#result eq null")
     * @ CachePut
     * @ CacheEvict
     */
    @Bean
    public CacheManager cacheManager(RedisTemplate jsonRedisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(jsonRedisTemplate);
        //设置缓存过期时间
        cacheManager.setDefaultExpiration(DEFAULTEXPIRETIME);
        cacheManager.setUsePrefix(true);
        cacheManager.setCachePrefix(cacheName -> ("cache:" + cacheName).getBytes());
        return cacheManager;
    }

    /**
     * jsonRedisTemplate
     * Jackson2JsonRedisSerializer来反序列化value值
     * StringRedisSerializer来序列化的key值
     */
    @Bean("jsonRedisTemplate")
    public RedisTemplate<Object, Object> jsonRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        //使用Jackson2JsonRedisSerializer来反序列化redis的value值
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(om);
        template.setValueSerializer(serializer);
        //使用StringRedisSerializer来序列化的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * StringRedisSerializer来序列化和反序列化的RedisTemplate
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }


    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }
}
