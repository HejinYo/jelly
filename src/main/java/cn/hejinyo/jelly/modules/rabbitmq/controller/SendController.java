package cn.hejinyo.jelly.modules.rabbitmq.controller;

import cn.hejinyo.jelly.common.utils.Result;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/7/12 21:41
 */
@RestController
@RequestMapping("/test/rabbit")
public class SendController {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 测试广播模式.
     */
    @RequestMapping("/fanout")
    public Result send(String p) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend("FANOUT_EXCHANGE", "", p, correlationData);
        return Result.ok();
    }

    /**
     * 测试Direct模式.
     */
    @RequestMapping("/direct")
    public Result direct(String p) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("DIRECT_EXCHANGE", "DIRECT_ROUTING_KEY", p, correlationData);
        return Result.ok();
    }
}