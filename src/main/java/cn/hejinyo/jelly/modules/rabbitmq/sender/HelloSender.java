package cn.hejinyo.jelly.modules.rabbitmq.sender;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : heshuangshuang
 * @date : 2018/7/12 17:51
 */
//Component
public class HelloSender {
    @Autowired
    private AmqpTemplate template;

    public void send() {
        template.convertAndSend("hello", "hello,rabbit~");
    }

}
