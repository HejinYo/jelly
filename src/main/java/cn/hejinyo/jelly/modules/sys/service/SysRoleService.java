package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysRole;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
public interface SysRoleService extends BaseService<SysRole, Integer> {

    /**
     * 角色授权
     */
    int operationPermission(int roleId, List<RolePermissionTreeDTO> rolePermissionList);

    /**
     * 角色列表下拉选择select
     */
    List<SysRole> roleSelect();
}
