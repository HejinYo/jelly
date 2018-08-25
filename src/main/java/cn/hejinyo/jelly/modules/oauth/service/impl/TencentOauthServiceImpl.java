package cn.hejinyo.jelly.modules.oauth.service.impl;

import cn.hejinyo.jelly.common.utils.JsonUtil;
import cn.hejinyo.jelly.common.utils.StringUtils;
import cn.hejinyo.jelly.modules.oauth.model.dto.TencentUserDTO;
import cn.hejinyo.jelly.modules.oauth.service.TencentOauthService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/8/25 22:49
 */
@Service
@Slf4j
public class TencentOauthServiceImpl implements TencentOauthService {

    @Value("${tencent.authorizeURL}")
    private String authorizeURL;

    @Value("${tencent.accessTokenURL}")
    private String accessTokenURL;

    @Value("${tencent.getOpenIDURL}")
    private String getOpenIDURL;

    @Value("${tencent.getUserInfoURL}")
    private String getUserInfoURL;

    @Value("${tencent.clientId}")
    private String clientId;

    @Value("${tencent.clientSecret}")
    private String clientSecret;

    @Value("${tencent.redirectURI}")
    private String redirectURI;

    @Value("${tencent.scope}")
    private String scope;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public String loginUrl() {
        return authorizeURL + "?response_type=code&state=" + UUID.randomUUID() + "&client_id=" + clientId + "&redirect_uri=" + redirectURI + "&scope=" + scope;
    }

    @Override
    public TencentUserDTO loginRedirect(String code, String state) {
        String accessTokenResp = restTemplate.getForEntity(accessTokenURL + "?grant_type=authorization_code&code={1}&client_id={2}&client_secret={3}&redirect_uri={4}",
                String.class, code, clientId, clientSecret, redirectURI).getBody();
        String accessToken = null;
        String expires = null;
        String refreshToken = null;
        if (StringUtils.isNotEmpty(accessTokenResp)) {
            log.info("accessTokenResp:{}", accessTokenResp);
            String[] res = accessTokenResp.split("&");
            for (String s : res) {
                String[] kv = s.split("=");
                switch (kv[0]) {
                    case "access_token":
                        accessToken = kv[1];
                        break;
                    case "expires_in":
                        expires = kv[1];
                        break;
                    case "refresh_token":
                        refreshToken = kv[1];
                        break;
                    default:
                }
            }
        }

        String openid = null;
        String openidResp = restTemplate.getForEntity(getOpenIDURL + "?access_token={1}", String.class, accessToken).getBody();
        if (StringUtils.isNotEmpty(openidResp)) {
            log.info("openidResp:{}", openidResp);
            openidResp = openidResp.replace("callback(", "").replace(");", "").trim();
            JSONObject jsonObject = JSON.parseObject(openidResp);
            openid = jsonObject.getString("openid");
            log.info("openid:{}", openid);
        }

        String userInfo = restTemplate.getForObject(getUserInfoURL + "?access_token={1}&oauth_consumer_key={2}&openid={3}", String.class, accessToken, clientId, openid);
        if (StringUtils.isNotEmpty(userInfo)) {
            log.info("userInfo:{}", userInfo);
            return JsonUtil.fromJson(userInfo, TencentUserDTO.class);
        }
        return null;
    }
}
