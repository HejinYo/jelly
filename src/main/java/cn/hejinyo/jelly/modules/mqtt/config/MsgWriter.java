package cn.hejinyo.jelly.modules.mqtt.config;

import org.springframework.integration.annotation.MessagingGateway;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/5/7 20:13
 */
@MessagingGateway(defaultRequestChannel = "outChannel")
public interface MsgWriter {
    void write(String note);
}
