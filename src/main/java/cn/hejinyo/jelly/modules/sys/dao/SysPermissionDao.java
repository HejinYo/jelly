package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysPermission;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 16:54
 */
@Mapper
public interface SysPermissionDao extends BaseDao<SysPermission, Integer> {
    /**
     * 查找用户编号对应的权限编码字符串
     */
    Set<String> getUserPermisSet(int userId);

    /**
     * 查找角色编号对应的权限编码字符串
     */
    Set<String> getRolePermissionSet(int roleId);

    /**
     * 查询指定resPid的资源
     */
    List<RolePermissionTreeDTO> findResourceList(int resPid);

    /**
     * 查询指定resCode的权限
     */
    List<RolePermissionTreeDTO> findPermissionList(String resCode);
}