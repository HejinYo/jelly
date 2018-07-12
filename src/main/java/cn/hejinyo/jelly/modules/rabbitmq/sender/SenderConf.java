package cn.hejinyo.jelly.modules.rabbitmq.sender;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author : heshuangshuang
 * @date : 2018/7/12 16:44
 */
@Component
public class SenderConf {
    @Bean
    public Queue queue() {
        // durable = true queue持久化
        return new Queue("hello", true);
    }
}
