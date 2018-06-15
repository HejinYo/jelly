package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysRolePermissionEntity;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
public interface SysRolePermissionService extends BaseService<SysRolePermissionEntity, Integer> {


    /**
     * 保存角色权限关系
     */
    int save(Integer roleId, List<Integer> permIdList);

    /**
     * 删除多个角色与权限关系
     */
    int deleteByRoleIds(Integer[] roleIds);

    /**
     * 权限与角色关系
     */
    int deleteByPermId(Integer permId);

}
