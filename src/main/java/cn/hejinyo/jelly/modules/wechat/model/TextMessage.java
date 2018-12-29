package cn.hejinyo.jelly.modules.wechat.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/13 11:46
 */
@XStreamAlias("xml")
@Data
public class TextMessage implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 开发者微信号
     */
    @XStreamAlias("ToUserName")
    private String toUserName;

    /**
     * 发送方帐号（一个OpenID）
     */
    @XStreamAlias("FromUserName")
    private String fromUserName;

    /**
     * 消息创建时间 （整型）
     */
    @XStreamAlias("CreateTime")
    private String createTime;

    /**
     * text
     */
    @XStreamAlias("MsgType")
    private String msgType;

    /**
     * 文本消息内容
     */
    @XStreamAlias("Content")
    private String content;

    /**
     * 消息id，64位整型
     */
    @XStreamAlias("MsgId")
    private String msgId;

}
