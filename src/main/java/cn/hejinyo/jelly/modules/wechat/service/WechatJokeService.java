package cn.hejinyo.jelly.modules.wechat.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.wechat.model.WechatJoke;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/23 22:23
 * @Description :
 */
public interface WechatJokeService extends BaseService<WechatJoke, Integer> {
    WechatJoke getRandomWechatJoke();

    String weater(String citys);
}
