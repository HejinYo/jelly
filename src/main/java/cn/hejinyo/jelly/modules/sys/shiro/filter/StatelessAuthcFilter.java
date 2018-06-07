package cn.hejinyo.jelly.modules.sys.shiro.filter;

import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.consts.UserToken;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
import cn.hejinyo.jelly.modules.sys.shiro.token.StatelessAuthcToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/29 18:05
 */
@Slf4j
public class StatelessAuthcFilter extends AccessControlFilter {

    private static final String DEFAULT_AUTHOR_PARAM = "Authorization";

    @Autowired
    private RedisUtils redisUtils;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        String userToken = ((HttpServletRequest) request).getHeader(DEFAULT_AUTHOR_PARAM);
        try {
            //从header 中获得 userToken
            //解析token
            String username = Tools.getTokenInfo(userToken, UserToken.USERNAME.getValue());
            //缓存中是否有此用户
            CurrentUserDTO userDTO = redisUtils.get(RedisKeys.getTokenCacheKey(username), CurrentUserDTO.class, 1800);
            if (null != userDTO) {
                //验证Token有效性
                Tools.verifyToken(userToken, userDTO.getUserPwd());
                //委托给Realm进行登录
                try {
                    getSubject(request, response).login(new StatelessAuthcToken(username, userToken, userDTO));
                } catch (Exception e) {
                    //userToken验证失败
                    log.debug("[ username:" + username + "] userToken验证失败：" + userToken);
                    ResponseUtils.response(response, Result.error(StatusCode.TOKEN_OVERDUE));
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            log.debug("非法的userToken：" + userToken);
        }
        ResponseUtils.response(response, Result.error(StatusCode.TOKEN_OVERDUE));
        return false;
    }

}

