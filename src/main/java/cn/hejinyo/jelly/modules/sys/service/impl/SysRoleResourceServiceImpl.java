package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.modules.sys.dao.SysRoleResourceDao;
import cn.hejinyo.jelly.modules.sys.model.SysRoleResource;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;
import cn.hejinyo.jelly.modules.sys.service.SysRoleResourceService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/3/3 13:02
 */
@Service
public class SysRoleResourceServiceImpl extends BaseServiceImpl<SysRoleResourceDao, SysRoleResource, Integer> implements SysRoleResourceService {

    /**
     * 删除角色原来所有权限
     */
    @Override
    public int deleteRolePermission(int roleId) {
        return baseDao.deleteRolePermission(roleId);
    }

    /**
     * 保存角色权限
     */
    @Override
    public int saveRolePermission(Integer roleId, List<RolePermissionTreeDTO> list) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("roleId", roleId);
        param.put("permission", list);
        return baseDao.saveRolePermission(param);
    }

    /**
     * 删除资源时删除角色授权资源
     */
    @Override
    public int deleteRoleRes(Integer resId) {
        return baseDao.deleteRoleRes(resId);
    }


    /**
     * 删除角色权限
     */
    @Override
    public int deleteRolePrem(Integer permId) {
        return baseDao.deleteRolePrem(permId);
    }
}
