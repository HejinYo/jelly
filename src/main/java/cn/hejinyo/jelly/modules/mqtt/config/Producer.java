package cn.hejinyo.jelly.modules.mqtt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/5/7 20:11
 */
//@Component
public class Producer {

    @Autowired
    private MqttPahoClientFactory mqttClientFactory;

    @Bean
    public IntegrationFlow mqttOutFlow() {
        //console input
        //        return IntegrationFlows.from(CharacterStreamReadingMessageSource.stdin(),
        //                e -> e.poller(Pollers.fixedDelay(1000)))
        //                .transform(p -> p + " sent to MQTT")
        //                .handle(mqttOutbound())
        //                .get();
        return IntegrationFlows.from(outChannel())
                .handle(mqttOutbound())
                .get();
    }

    @Bean
    public MessageChannel outChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("siSamplePublisher", mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("test");
        return messageHandler;
    }

}
