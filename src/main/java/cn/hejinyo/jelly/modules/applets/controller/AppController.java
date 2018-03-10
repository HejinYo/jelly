package cn.hejinyo.jelly.modules.applets.controller;

import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.applets.model.WeUser;
import cn.hejinyo.jelly.modules.applets.model.dto.JscodeToSessionDTO;
import cn.hejinyo.jelly.modules.applets.service.WeUserService;
import com.alibaba.druid.support.json.JSONUtils;
import com.google.gson.Gson;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

/**
 * @author : heshuangshuang
 * @date : 2018/3/9 15:08
 */
@RestController
@RequestMapping("/app")
public class AppController {
    private static final Logger logger = LoggerFactory.getLogger(AppController.class);
    private static final String tokenKey = "hejinyo";

    @Autowired
    private WeUserService weUserService;

    private final static String appid = "wxe3a80dbb59e8d177";
    private final static String secret = "37d8098efec6cb47ec4601fc40df943f";
    private final static String authorization_code = "hejinyo_authorization_code";

    @RequestMapping("/login")
    public Result login(@RequestParam HashMap<String, Object> params) {
        System.out.println(JSONUtils.toJSONString(params));
        String code = MapUtils.getString(params, "code");
        HashMap<String, String> param = new HashMap<>();
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        param.put("appid", appid);
        param.put("secret", secret);
        param.put("js_code", code);
        param.put("grant_type", authorization_code);
        String result = HttpClientUtil.getInstance().sendHttpPost(url, param);
        logger.debug(JsonUtils.toJSONString(result));
        return Result.error(result);
    }

    /**
     * 小程序登录
     * 登录流程：
     * 1、拿到jscode请求微信后端，获取用户的openid 和 session_key
     * 2、生成用户token，作为key,value为openid和session_key保存在服务端，将生成的token发送给小程序
     * 3、小程序每次请求服务器，带上token,根据token获取openid和session_key来识别用户唯一性
     * 4、signature = sha1( rawData + session_key )
     */
    @RequestMapping("/jscode2session")
    public Result jscode2session(@RequestParam HashMap<String, Object> params) {
        System.out.println(JSONUtils.toJSONString(params));
        String code = MapUtils.getString(params, "jsCode");
        HashMap<String, String> param = new HashMap<>();
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        param.put("appid", appid);
        param.put("secret", secret);
        param.put("js_code", code);
        param.put("grant_type", authorization_code);
        String result = HttpClientUtil.getInstance().sendHttpPost(url, param);
        JscodeToSessionDTO sessionDTO = JsonUtils.toObject(result, JscodeToSessionDTO.class);
        if (StringUtils.isNotEmpty(sessionDTO.getOpenid())) {
            WeUser weUser = weUserService.findOneByOpenId(sessionDTO.getOpenid());
            if (weUser == null) {
                //创建新用户，绑定微信
                weUser = JsonUtils.toObject(MapUtils.getString(params, "userInfo"), WeUser.class);
                weUser.setOpenId(sessionDTO.getOpenid());
                weUser.setCreateTime(new Date());
                weUser.setState(0);
                //保存新用户
                weUserService.save(weUser);
            } else {
                //更新用户信息
                weUserService.update(weUser);
            }
            //生成UserToken，并返回
            String token = Tools.createToken(2, weUser.getUserId(), weUser.getNickName(), tokenKey);
            return Result.ok(token);
        }
        //{"session_key":"dphgiRox+mngPanYpkmUvA==","openid":"orXbq4l36DC9HSZRV9jsIqvkJMNM"}
        logger.debug("获得用户session失败:{}", JsonUtils.toJSONString(sessionDTO));
        return Result.error(sessionDTO.getErrmsg());
    }

    /**
     * 小程序登录
     *
     * @return
     */
    @RequestMapping("/test")
    public Result test(@RequestParam HashMap<String, Object> params) {
        /**
         *
         {"jsCode":"0012VG0V0mujKU1x7E2V0g311V02VG0l","nickName":"HejinYo","sign":"e1cc51ce628144f097e123c609e3b171","time":"20180310150920"}
         */
        System.out.println(JSONUtils.toJSONString(params));
        return Result.ok(params);
    }


}
