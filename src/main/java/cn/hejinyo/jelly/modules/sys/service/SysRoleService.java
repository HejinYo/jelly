package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.modules.sys.model.SysRole;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;
import cn.hejinyo.jelly.modules.sys.model.dto.RoleResourceDTO;

import java.util.List;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 * @Description :
 */
public interface SysRoleService extends BaseService<SysRole, Integer> {

    /**
     * 查找用户编号对应的角色编码字符串
     */
    Set<String> getUserRoleSet(int userId);

    /**
     * 查询角色权限列表
     */
    List<RoleResourceDTO> findPageForRoleResource(PageQuery pageQuery);

    int operationPermission(int roleId, List<RolePermissionTreeDTO> rolePermissionList);

    List<SysRole> roleSelect();
}
