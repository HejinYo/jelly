package cn.hejinyo.jelly.common.utils;

import com.google.gson.reflect.TypeToken;
import jodd.util.RandomString;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/8 20:53
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisUtilsTest {
    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void delete() {
        redisUtils.delete("map");
    }


    @Test
    public void cleanKey() {
        redisUtils.cleanKey("key*");
    }

    @Test
    public void exists() {
        redisUtils.set("key", "key");
        assertEquals(true, redisUtils.exists("key"));
        redisUtils.delete("key");
        assertEquals(false, redisUtils.exists("key"));
    }

    @Test
    public void pExpire() {
        assertEquals(false, redisUtils.pExpire("key", 10000));
        redisUtils.set("key", "key");
        assertEquals(true, redisUtils.pExpire("key", 10000));
        assert redisUtils.ttl("key") > 0;
        redisUtils.delete("key");
        assert -2 == redisUtils.ttl("key");
    }

    @Test
    public void renameNX() {
        redisUtils.set("key1", "key1");
        redisUtils.set("key2", "key2");
        assertFalse(redisUtils.renameNX("key2", "key1"));
        assertTrue(redisUtils.renameNX("key2", "key3"));
    }

    @Test
    public void set1() {
        redisUtils.set("user", getOne());
    }

    @Test
    public void setNX() {
        assertTrue(redisUtils.setNX("user2", getOne()));
        assertFalse(redisUtils.setNX("user2", getOne()));
    }

    @Test
    public void get() {
        System.out.println(redisUtils.get("user2"));
    }

    @Test
    public void get1() {
        System.out.println(redisUtils.get("user2", Student.class).getName());
    }

    @Test
    public void bLPop() {
        redisUtils.lpush("list", getOne());
        System.out.println(redisUtils.bLPop("list", 100));
        System.out.println(redisUtils.bLPop("list", 100, Student.class).getName());
    }

    @Test
    public void hget() {
        Set<String> roleSet = new HashSet<>();
        roleSet.add("admin");
        roleSet.add("ddd");
        roleSet.add("ff");
        //redisUtils.hsetEX(RedisKeys.storeUser(1), RedisKeys.USER_ROLE, roleSet, 600);
        Set<String> roleSet2 = redisUtils.hget(RedisKeys.storeUser(1), RedisKeys.USER_ROLE, new TypeToken<Set<String>>() {
        }.getType());
        System.out.println(roleSet2.iterator().next());
    }

    public static void main(String[] args) {
        String.valueOf(null);
    }


    private Student getOne() {
        Student s = new Student();
        Random random = new Random(100);
        s.setName(RandomString.getInstance().randomAlpha(10));
        s.setId(random.nextLong());
        s.setSex(random.nextInt(100));
        s.setAddr(s.getName() + s.getId());
        return s;
    }


    @Data
    private class Student {
        private String name;
        private Long id;
        private Integer sex;
        private String addr;
    }
}