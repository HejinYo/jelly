package cn.hejinyo.jelly.modules.applets;

import cn.hejinyo.jelly.common.utils.HttpClientUtil;
import cn.hejinyo.jelly.common.utils.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author : heshuangshuang
 * @date : 2018/3/9 15:08
 */
@RestController
@RequestMapping("/app")
public class AppController {
    private final static String appid = "wxe3a80dbb59e8d177";
    private final static String secret = "37d8098efec6cb47ec4601fc40df943f";
    private final static String authorization_code = "hejinyo_authorization_code";
    private final static String myOpenId = "orXbq4l36DC9HSZRV9jsIqvkJMNM";

    /**
     * 小程序登录
     *
     * @return
     */
    @RequestMapping("/login")
    public Result login(@RequestParam String code) {
        HashMap<String, String> param = new HashMap<>();
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        param.put("appid", appid);
        param.put("secret", secret);
        param.put("js_code", code);
        param.put("grant_type", authorization_code);
        String result = HttpClientUtil.getInstance().sendHttpPost(url, param);
        return Result.ok(result);
    }


}
