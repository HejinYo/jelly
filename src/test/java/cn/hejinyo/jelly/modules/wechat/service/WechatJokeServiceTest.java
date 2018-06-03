package cn.hejinyo.jelly.modules.wechat.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/2 19:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WechatJokeServiceTest {

    @Autowired
    private ListOperations<String, Object> listOperations;

    @Test
    public void test() {
        listOperations.leftPush("test", "123");
        Object uu = listOperations.rightPopAndLeftPush("test", "test:new");
        System.out.println(uu);
        Object uuu = listOperations.rightPopAndLeftPush("test", "test:new");
        System.out.println(uuu);

    }

    public static void main(String[] args) throws InterruptedException {
        Long currTimes = System.currentTimeMillis();
        System.out.println(currTimes);
        System.out.println(System.currentTimeMillis());
        Thread.sleep(1000);
        System.out.println(System.currentTimeMillis());
    }
}