package cn.hejinyo.jelly.modules.sys.shiro.filter;

import cn.hejinyo.jelly.common.consts.UserToken;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/6 15:07
 */
public class LogoutFilter extends AdviceFilter {
    private static final String DEFAULT_AUTHOR_PARAM = "Authorization";

    @Autowired
    private RedisUtils redisUtils;

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        String userToken = ((HttpServletRequest) request).getHeader(DEFAULT_AUTHOR_PARAM);
        if (userToken != null) {
            //token中获取用户名
            String username = Tools.getTokenInfo(userToken, UserToken.USERNAME.getValue());
            //查询缓存中的用户信息
            CurrentUserDTO userDTO = redisUtils.get(RedisKeys.getTokenCacheKey(username), CurrentUserDTO.class, 1800);
            if (null != userDTO) {
                try {
                    //验证token是否有效
                    Tools.verifyToken(userToken, userDTO.getUserPwd());
                    //清除token缓存
                    redisUtils.delete(RedisKeys.getTokenCacheKey(userDTO.getUserName()));
                    //清除授权缓存
                    redisUtils.delete(RedisKeys.getAuthCacheKey(userDTO.getUserName()));
                } catch (Exception ignored) {

                }
            }
        }
        ResponseUtils.response(response, Result.ok("退出登录成功"));
        return false;
    }
}
