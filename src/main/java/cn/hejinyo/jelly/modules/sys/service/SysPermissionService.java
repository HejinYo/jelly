package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysPermission;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;

import java.util.List;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:06
 */
public interface SysPermissionService extends BaseService<SysPermission, Integer> {
    /**
     * 权限资源是否存在
     *
     * @return
     */
    boolean isExist(SysPermission sysPermission);

    /**
     * 查找角色编号对应的权限编码字符串
     */
    Set<String> getRolePermissionSet(int roleId);

    /**
     * 获得授权树
     */
    List<RolePermissionTreeDTO> getResourcePermissionTree();

    /**
     * 删除资源对应权限数据
     */
    int deletePermByResCode(String resCode);

    /**
     * 根据resId更新resCode
     */
    int updateResCodeByResId(SysPermission permission);

}
