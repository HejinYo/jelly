package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/3/20 22:52
 */
@Mapper
public interface ShiroDao {
    /**
     * 执行登录，查询用户登录信息
     */
    LoginUserDTO findLoginUser(String userName);

    /**
     * 查找所有角色编码字符串，管理员使用
     */
    Set<String> findAllRoleSet();

    /**
     * 查找所有授权编码字符串，管理员使用
     */
    Set<String> findAllPermSet();

    /**
     * 查找用户编号对应的角色编码字符串
     */
    Set<String> findUserRoleSet(int userId);

    /**
     * 查找用户编号对应的权限编码字符串
     */
    Set<String> findUserPermSet(int userId);

}
