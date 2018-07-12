package cn.hejinyo.jelly.modules.rabbitmq.receiver;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * MQ消费者
 *
 * @author : heshuangshuang
 * @date : 2018/7/12 16:44
 */
@Component
public class HelloReceiver {

    @RabbitListener(queues = "hello")
    public void process(Message msg, Channel channel) throws IOException {
        System.out.println("Receiver : " + msg);
        // 确认消息
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), true);
    }

}