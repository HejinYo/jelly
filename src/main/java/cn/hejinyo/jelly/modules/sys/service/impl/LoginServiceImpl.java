package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.sys.model.SysUserEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.model.dto.PhoneLoginDTO;
import cn.hejinyo.jelly.modules.sys.service.LoginService;
import cn.hejinyo.jelly.modules.sys.service.ShiroService;
import cn.hejinyo.jelly.modules.sys.service.SysUserService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 发送电话登录验证码
     */
    @Override
    public boolean sendPhoneCode(String phone) {
        // 验证电话号码格式
        String regEx = "^$|^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(phone);
        if (!matcher.find()) {
            throw new InfoException("电话号码格式错误");
        }
        String code = String.valueOf(RandomUtils.nextInt(100000, 999999));
        boolean result = SmsUtils.sendLogin(phone, code);
        if (result) {
            redisUtils.setEX(RedisKeys.smsSingle(phone), code, 300);
        }
        return result;
    }

    /**
     * 手机用户登录
     */
    @Override
    public LoginUserDTO phoneLogin(PhoneLoginDTO phoneLogin) {
        String phone = phoneLogin.getPhone();
        String code = phoneLogin.getCode();
        // 匹配验证码
        String localCode = redisUtils.get(RedisKeys.smsSingle(phone), String.class);
        if (StringUtils.isEmpty(localCode)) {
            throw new InfoException("验证码过期，请重新获取");
        }
        // 删除验证码
        redisUtils.delete(RedisKeys.smsSingle(phone));
        if (!code.equals(localCode)) {
            throw new InfoException("验证码错误");
        }
        // 根据号码查询用户
        LoginUserDTO userDTO = shiroService.getPhoneUser(phone);
        if (null == userDTO) {
            // 创建一个用户
            SysUserEntity sysUser = new SysUserEntity();
            sysUser.setPhone(phone);
            sysUser.setUserName(phone);
            sysUser.setUserPwd("123456");
            sysUser.setNickName("游客");
            sysUser.setAvatar("http://ow1prafcd.bkt.clouddn.com/hejinyo.jpg");
            sysUser.setRoleIdList(Collections.singletonList(1));
            sysUser.setDeptIdList(Collections.singletonList(1));
            sysUser.setState(0);
            sysUserService.save(sysUser);
            userDTO = shiroService.getPhoneUser(phone);
        }

        // 如果无相关用户或已删除则返回null
        if (null == userDTO) {
            throw new InfoException(StatusCode.LOGIN_USER_NOEXIST);
        } else if (1 == userDTO.getState()) {
            throw new InfoException(StatusCode.LOGIN_USER_LOCK);
        }
        return userDTO;
    }


    /**
     * 验证登录用户
     */
    @Override
    public LoginUserDTO checkUser(LoginUserDTO loginUser) {
        // 根据用户名查找用户，进行密码匹配
        LoginUserDTO userDTO = shiroService.getLoginUser(loginUser.getUserName());
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
        if (!password.equals(ShiroUtils.userPassword(loginUser.getUserPwd(), salt))) {
            throw new InfoException(StatusCode.LOGIN_PASSWORD_ERROR);
        }
        return userDTO;
    }

    /**
     * 处理登录逻辑
     */
    @Override
    public String doLogin(LoginUserDTO userDTO) {
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
