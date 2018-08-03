package cn.hejinyo.jelly.modules.oauth.controller;

import cn.hejinyo.jelly.common.utils.JsonUtil;
import cn.hejinyo.jelly.common.utils.Result;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : heshuangshuang
 * @date : 2018/8/3 16:48
 */
@RestController
@RequestMapping("/oauth")
@Slf4j
public class OauthController {

    @GetMapping("/login")
    public Result loginPage(HttpServletRequest request) throws QQConnectException {
        return Result.result(new Oauth().getAuthorizeURL(request));
    }

    @GetMapping("/v2")
    public Result LoginRedirect(HttpServletRequest request) throws QQConnectException {
        AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
        System.out.println(JsonUtil.toJson(accessTokenObj));

        //我们的网站被CSRF攻击了或者用户取消了授权,做一些数据统计工作
        if ("".equals(accessTokenObj.getAccessToken())) {
            return Result.ok("没有获取到响应参数");
        }
        String accessToken = accessTokenObj.getAccessToken();
        long tokenExpireIn = accessTokenObj.getExpireIn();
        log.debug("accessToken:{}", accessToken);
        log.debug("tokenExpireIn:{}", tokenExpireIn);

        OpenID openIDObj = new OpenID(accessToken);
        String openID = openIDObj.getUserOpenID();
        log.debug("openID:{}", openID);
        UserInfo userInfo = new UserInfo(accessToken, openID);
        UserInfoBean userInfoBean = userInfo.getUserInfo();
        if (userInfoBean.getRet() == 0) {
            log.debug("openID:{}", JsonUtil.toJson(userInfoBean));
            return Result.ok("登录成功", userInfoBean);
        }
        return Result.ok("很抱歉，我们没能正确获取到您的信息", userInfoBean.getMsg());
    }
}
