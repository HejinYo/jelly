package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.modules.sys.model.SysRole;
import cn.hejinyo.jelly.modules.sys.model.dto.RoleResourceDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 16:54
 */
@Mapper
public interface SysRoleDao extends BaseDao<SysRole, Integer> {

    /**
     * 查找用户编号对应的角色编码字符串
     */
    Set<String> getUserRoleSet(int userId);

    /**
     * 查询角色权限列表
     */
    List<RoleResourceDTO> findPageForRoleResource(PageQuery pageQuery);

    /**
     * 删除角色原来所有权限
     */
    int deleteRolePermission(int roleId);

    /**
     * 角色列表下拉选择select
     */
    List<SysRole> roleSelect();
}