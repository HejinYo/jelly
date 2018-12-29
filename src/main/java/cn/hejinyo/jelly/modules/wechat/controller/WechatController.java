package cn.hejinyo.jelly.modules.wechat.controller;

import cn.hejinyo.jelly.common.utils.JsonUtil;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.utils.StringUtils;
import cn.hejinyo.jelly.common.utils.XmlUtils;
import cn.hejinyo.jelly.modules.wechat.model.ReplyMessage;
import cn.hejinyo.jelly.modules.wechat.model.TextMessage;
import cn.hejinyo.jelly.modules.wechat.service.WechatJokeService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/13 10:48
 * @Description :
 */
@Controller
@RequestMapping(value = "/wechat")
public class WechatController {
    private static final String myToken = "hejinyo";

    @Autowired
    private WechatJokeService wechatJokeService;

    @RequestMapping("/findOne")
    @ResponseBody
    public Result joke() {
        return Result.ok(wechatJokeService.getRandomWechatJoke());
    }

    @GetMapping
    public String joinWechat(@RequestParam String signature, @RequestParam String timestamp, @RequestParam String nonce, @RequestParam String echostr) {
        String requeststr = "signature:" + signature + ";timestamp" + timestamp + ";nonce:" + nonce + ";echostr:" + echostr;
        System.out.println("微信验证参数：" + requeststr);
        //字典序排序
        ArrayList<String> list = new ArrayList<>();
        list.add(nonce);
        list.add(timestamp);
        list.add(myToken);
        Collections.sort(list);

        String localsignature = DigestUtils.sha1Hex(list.get(0) + list.get(1) + list.get(2));
        if (signature.equals(localsignature)) {
            System.out.println("微信认证通过：" + localsignature);
            return echostr;
        }
        return echostr;
    }

    public static void main(String[] args) {
    }

    @PostMapping
    @ResponseBody
    public String replyMessage(@RequestBody String param) {
        System.out.println("param:" + param);
        TextMessage message = JsonUtil.fromJson(JsonUtil.toJson(XmlUtils.parseFromXml(TextMessage.class, param)), TextMessage.class);
        ReplyMessage replyMessage = new ReplyMessage();
        replyMessage.setFromUserName(message.getToUserName());
        replyMessage.setToUserName(message.getFromUserName());
        replyMessage.setCreateTime(String.valueOf(System.currentTimeMillis()));
        replyMessage.setMsgType(message.getMsgType());
        String mess = message.getContent();
        if (mess.contains("天气")) {
            String citys = mess.replace("天气", "").replace(" ", "");
            if (StringUtils.isEmpty(citys.trim())) {
                citys = "北京";
            }
            replyMessage.setContent(wechatJokeService.weater(citys.trim()));
        } else if (mess.contains("笑话")) {
            replyMessage.setContent(wechatJokeService.getRandomWechatJoke().getContent());
        } else {
            replyMessage.setMediaId("100000003");
            replyMessage.setContent(mess);
        }

        System.out.println("replyMessage" + replyMessage);
        return replyMessage.toString();
    }
}
