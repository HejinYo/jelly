package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
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
     *
     * @param userName
     * @return
     */
    CurrentUserDTO getCurrentUser(String userName);

    /**
     * 查找用户编号对应的角色编码字符串
     */
    Set<String> getUserRoleSet(int userId);

    /**
     * 查找用户编号对应的权限编码字符串
     */
    Set<String> getUserPermisSet(int userId);

    /**
     * 根据查询所有权限列表字符串
     */
    Set<String> getAllPermisSet();

}
