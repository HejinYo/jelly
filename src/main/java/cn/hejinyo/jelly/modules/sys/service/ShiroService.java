package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;

import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/3/20 22:48
 */
public interface ShiroService {
    /**
     * 手机登录，查询用户登录信息
     */
    LoginUserDTO getPhoneUser(String phone);

    /**
     * 执行登录，查询用户登录信息
     */
    LoginUserDTO getLoginUser(String userName);

    /**
     * 查找用户编号对应的角色编码字符串
     */
    Set<String> getUserRoleSet(int userId);

    /**
     * 查找用户编号对应的权限编码字符串
     */
    Set<String> getUserPermSet(int userId);
}
