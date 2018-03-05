package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * sys_user_role 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/03/03 19:07
 */
@Mapper
public interface SysUserRoleDao extends BaseDao<SysUserRole, Integer> {

    /**
     * 根据用户id删除用户角色关系
     *
     * @param ids userId数组
     */
    int deleteListByUserId(Integer[] ids);
}