package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysUser;
import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;

import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
public interface SysUserService extends BaseService<SysUser, Integer> {

    /**
     * 执行登录，查询用户登录信息
     *
     * @param userName
     * @return
     */
    CurrentUserDTO getCurrentUser(String userName);

    /**
     * 用户名是否存在
     *
     * @param userName
     * @return
     */
    boolean isExistUserName(String userName);

    /**
     * 更新用户登录信息
     *
     * @param userDTO
     * @return
     */
    int updateUserLoginInfo(CurrentUserDTO userDTO);

    /**
     * 修改密码
     */
    int updatePassword(HashMap<String, Object> param);

    /**
     * 修改个人信息
     */
    int updateUserInfo(SysUser sysUser);

    /**
     * 修改头像
     */
    int updateUserAvatar(SysUser sysUser);

    /**
     * 更新用户redis信息
     */
    void updateUserRedisInfo();
}
