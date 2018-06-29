package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.common.utils.Tools;
import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.service.LoginService;
import cn.hejinyo.jelly.modules.sys.service.ShiroService;
import cn.hejinyo.jelly.modules.sys.service.SysUserService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统登录业务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/8 22:56
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ShiroService shiroService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 验证登录用户
     */
    @Override
    public LoginUserDTO checkUser(String userName, String userPwd) {
        // 根据用户名查找用户，进行密码匹配
        LoginUserDTO userDTO = shiroService.getLoginUser(userName);
        // 如果无相关用户或已删除则返回null
        if (null == userDTO) {
            throw new InfoException(StatusCode.LOGIN_USER_NOEXIST);
        } else if (1 == userDTO.getState()) {
            throw new InfoException(StatusCode.LOGIN_USER_LOCK);
        }
        //获取用户数据库中密码
        String password = userDTO.getUserPwd();
        //获取用户盐
        String salt = userDTO.getUserSalt();
        //验证密码
        if (!password.equals(ShiroUtils.userPassword(userPwd, salt))) {
            throw new InfoException(StatusCode.LOGIN_PASSWORD_ERROR);
        }
        return userDTO;
    }

    /**
     * 处理登录逻辑
     */
    @Override
    public String doLogin(String userName, String userPwd) {
        LoginUserDTO userDTO = checkUser(userName, userPwd);
        Integer userId = userDTO.getUserId();
        //创建jwt token
        String token = Tools.createToken(userId, userDTO.getUserName(), userDTO.getUserPwd());
        //设置token
        userDTO.setUserToken(token);
        //清除用户原来所有缓存
        redisUtils.delete(RedisKeys.storeUser(userId));
        //token写入缓存
        redisUtils.hsetEX(RedisKeys.storeUser(userId), RedisKeys.USER_TOKEN, userDTO, Constant.USER_TOKEN_EXPIRE);
        //记录本次登录Ip和时间
        sysUserService.updateUserLoginInfo(userId);
        return token;
    }
}
