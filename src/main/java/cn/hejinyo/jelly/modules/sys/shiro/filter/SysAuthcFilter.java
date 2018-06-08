package cn.hejinyo.jelly.modules.sys.shiro.filter;

import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.shiro.token.SysAuthcToken;
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
public class SysAuthcFilter extends AccessControlFilter {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        String userToken = ((HttpServletRequest) request).getHeader(Constant.AUTHOR_PARAM);
        try {
            //从header 中获得 userToken
            Integer userId = Tools.tokenInfoInt(userToken, Constant.JWT_TOKEN_USERID);
            //缓存中是否有此用户
            LoginUserDTO userDTO = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_TOKEN, LoginUserDTO.class);
            if (null != userDTO) {
                //验证Token有效性
                Tools.verifyToken(userToken, userDTO.getUserPwd());
                //委托给Realm进行登录
                try {
                    getSubject(request, response).login(new SysAuthcToken(userId, userToken, userDTO));
                } catch (Exception e) {
                    // userToken验证失败
                    log.debug("[ userId:" + userId + "] userToken验证失败：" + userToken);
                    ResponseUtils.response(response, Result.error(StatusCode.TOKEN_OUT));
                    return false;
                }
                return true;
            }
            // token不在缓存中
            ResponseUtils.response(response, Result.error(StatusCode.TOKEN_OVERDUE));
        } catch (Exception e) {
            log.debug("非法的userToken：" + userToken);
            ResponseUtils.response(response, Result.error(StatusCode.TOKEN_FAULT));
        }
        return false;
    }

}

