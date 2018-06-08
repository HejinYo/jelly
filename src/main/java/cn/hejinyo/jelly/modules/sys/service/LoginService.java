package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/8 22:56
 */
public interface LoginService {


    /**
     * 验证登录用户
     */
    LoginUserDTO checkUser(String userName, String userPwd);

    /**
     * 处理登录逻辑
     */
    LoginUserDTO doLogin(String userName, String userPwd);

}
