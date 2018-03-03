package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysRoleResource;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;

import java.util.HashMap;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
public interface SysRoleResourceService extends BaseService<SysRoleResource, Integer> {

    /**
     * 保存角色权限
     */
    int saveRolePermission(Integer roleId, List<RolePermissionTreeDTO> list);
}
