package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.dao.ShiroDao;
import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.service.ShiroService;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/3/20 22:48
 */
@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ShiroDao shiroDao;

    /**
     * 执行登录，查询用户登录信息
     */
    @Override
    public LoginUserDTO getLoginUser(String userName) {
        return shiroDao.findLoginUser(userName);
    }

    /**
     * 查找用户编号对应的角色编码字符串
     */
    @Override
    public Set<String> getUserRoleSet(int userId) {
        // redis中获得角色信息
        Set<String> roleSet = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_ROLE, new TypeToken<Set<String>>() {
        }.getType());

        // 不存在则数据库获得角色信息
        if (roleSet == null) {
            // 超级管理拥有所有角色编码
            roleSet = userId == Constant.SUPER_ADMIN ? shiroDao.findAllRoleSet() : shiroDao.findUserRoleSet(userId);
            roleSet.removeIf(Objects::isNull);
            redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_ROLE, roleSet);
        }
        return roleSet;
    }

    /**
     * 查找用户编号对应的权限编码字符串
     */
    @Override
    public Set<String> getUserPermSet(int userId) {
        // redis中获得权限信息
        Set<String> permissionsSet = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_PERM, new TypeToken<Set<String>>() {
        }.getType());

        // 不存在则数据库获得权限信息
        if (permissionsSet == null) {
            // 超级管理拥有所有权限编码
            permissionsSet = userId == Constant.SUPER_ADMIN ? shiroDao.findAllPermSet() : shiroDao.findUserPermSet(userId);
            permissionsSet.removeIf(Objects::isNull);
            redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_PERM, permissionsSet);
        }
        return permissionsSet;
    }

}
