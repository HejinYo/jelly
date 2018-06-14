package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sys_user_role 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/14 23:44
 */
@Mapper
public interface SysUserRoleDao extends BaseDao<SysUserRoleEntity, Integer> {

    /**
     * 根据用户拥有的角色ID列表
     */
    List<Integer> findRoleIdListByUserId(Integer userId);

    /**
     * 删除用户角色关系
     */
    int deleteByUserId(Integer userId);
}