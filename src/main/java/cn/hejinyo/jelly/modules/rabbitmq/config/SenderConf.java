package cn.hejinyo.jelly.modules.rabbitmq.config;

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
        return new Queue("test");
    }
}
