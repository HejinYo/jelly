package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.dao.SysRoleDao;
import cn.hejinyo.jelly.modules.sys.model.SysRole;
import cn.hejinyo.jelly.modules.sys.model.SysUserRole;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;
import cn.hejinyo.jelly.modules.sys.service.SysRoleResourceService;
import cn.hejinyo.jelly.modules.sys.service.SysRoleService;
import cn.hejinyo.jelly.modules.sys.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
@Service
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleDao, SysRole, Integer> implements SysRoleService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysRoleResourceService sysRoleResourceService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 角色授权
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int operationPermission(int roleId, List<RolePermissionTreeDTO> rolePermissionList) {
        //清除redis中的权限缓存
        redisUtils.cleanKey(RedisKeys.getAuthCacheKey("*"));

        //删除原来所有的授权
        sysRoleResourceService.deleteRolePermission(roleId);
        if (rolePermissionList.size() == 0) {
            return 1;
        }
        //lamuda表达式
        rolePermissionList.removeIf(permissionTree -> permissionTree.getType().equals("resource"));
        if (rolePermissionList.size() > 0) {
            return sysRoleResourceService.saveRolePermission(roleId, rolePermissionList);
        }

        throw new InfoException("没有选择任何有效权限");
    }

    /**
     * 角色列表下拉选择select
     */
    @Override
    public List<SysRole> roleSelect() {
        return baseDao.roleSelect();
    }


    @Override
    public int deleteBatch(Integer[] ids) {
        for (int roleId : ids) {
            //删除用户角色表记录
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(roleId);
            List<SysUserRole> list = sysUserRoleService.findList(sysUserRole);
            if (list.size() > 0) {
                throw new InfoException("角色 [" + baseDao.findOne(roleId).getRoleName() + "] 存在用户，请取消后删除");
            }
        }
        return baseDao.deleteBatch(ids);
    }
}
