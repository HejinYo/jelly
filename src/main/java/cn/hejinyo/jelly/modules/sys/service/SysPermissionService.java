package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysPermissionEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.AuthTreeDTO;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:06
 */
public interface SysPermissionService extends BaseService<SysPermissionEntity, Integer> {
    /**
     * 获取系统所有权限列表
     */
    List<SysPermissionEntity> getAllPermissionList();

    /**
     * 获得授权树
     */
    List<AuthTreeDTO> getAuthTree();

    /**
     * 根据资源编号查询所有权限
     */
    List<SysPermissionEntity> findListByResId(Integer resId);
}
