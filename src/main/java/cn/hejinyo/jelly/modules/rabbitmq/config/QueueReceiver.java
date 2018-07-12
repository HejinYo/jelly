package cn.hejinyo.jelly.modules.rabbitmq.config;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author : heshuangshuang
 * @date : 2018/7/12 16:44
 */
//@Component
public class QueueReceiver {

    @RabbitListener(queues = "test")
    public void process(String msg) {
        System.out.println("Receiver : " + msg);
    }

}