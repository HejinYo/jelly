package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.model.dto.PhoneLoginDTO;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/8 22:56
 */
public interface LoginService {

    /**
     * 发送电话登录验证码
     */
    boolean sendPhoneCode(String phone);

    /**
     * 手机用户登录
     */
    LoginUserDTO phoneLogin(PhoneLoginDTO phoneLogin);


    /**
     * 验证登录用户
     */
    LoginUserDTO checkUser(LoginUserDTO loginUser);

    /**
     * 处理登录逻辑
     */
    String doLogin(LoginUserDTO userDTO);
}
