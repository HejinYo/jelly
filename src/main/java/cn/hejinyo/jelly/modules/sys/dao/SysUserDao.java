package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysUser;
import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/9 14:59
 */
@Mapper
public interface SysUserDao extends BaseDao<SysUser, Integer> {

    /**
     * 执行登录，查询用户登录信息
     *
     * @param userName
     * @return
     */
    CurrentUserDTO getCurrentUser(String userName);

    int saveUserRole(SysUser sysUser);

    int updateUserRole(SysUser sysUser);
}
