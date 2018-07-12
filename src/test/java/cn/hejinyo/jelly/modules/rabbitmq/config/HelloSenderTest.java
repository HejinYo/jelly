package cn.hejinyo.jelly.modules.rabbitmq.config;

import cn.hejinyo.jelly.modules.rabbitmq.sender.HelloSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author : heshuangshuang
 * @date : 2018/7/12 17:55
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class HelloSenderTest {

    @Autowired
    private HelloSender helloSender;

    @Test
    public void testRabbit() {
        helloSender.send();
    }
}