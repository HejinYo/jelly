package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.dao.SysRoleDao;
import cn.hejinyo.jelly.modules.sys.model.SysRoleEntity;
import cn.hejinyo.jelly.modules.sys.model.SysRolePermissionEntity;
import cn.hejinyo.jelly.modules.sys.model.SysUserRoleEntity;
import cn.hejinyo.jelly.modules.sys.service.SysRolePermissionService;
import cn.hejinyo.jelly.modules.sys.service.SysRoleService;
import cn.hejinyo.jelly.modules.sys.service.SysUserRoleService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色管理业务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
@Service
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleDao, SysRoleEntity, Integer> implements SysRoleService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 角色下拉列表
     */
    @Override
    public List<SysRoleEntity> getDropList() {
        return baseDao.findDropList();
    }

    @Override
    public SysRoleEntity findOne(Integer roleId) {
        SysRoleEntity sysRole = baseDao.findOne(roleId);
        if (sysRole != null) {
            //查询角色拥有的权限
            SysRolePermissionEntity rolePermission = new SysRolePermissionEntity();
            rolePermission.setRoleId(roleId);
            sysRole.setPermIdList(sysRolePermissionService.findList(rolePermission).stream().map(SysRolePermissionEntity::getPermId).collect(Collectors.toList()));
        }
        return sysRole;
    }

    /**
     * 保存角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(SysRoleEntity role) {
        // 从新构建保存对象，控制写入数据
        SysRoleEntity newRole = new SysRoleEntity();
        newRole.setRoleCode(role.getRoleCode());
        newRole.setRoleName(role.getRoleName());
        newRole.setDescription(role.getDescription());
        newRole.setState(role.getState());
        newRole.setCreateId(ShiroUtils.getUserId());
        int count = baseDao.save(newRole);
        if (count > 0) {
            //保存角色权限关系
            sysRolePermissionService.save(newRole.getRoleId(), role.getPermIdList());
            return newRole.getRoleId();
        }
        return count;
    }

    /**
     * 修改角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Integer roleId, SysRoleEntity role) {
        if (Constant.TREE_ROOT.equals(roleId)) {
            throw new InfoException(StatusCode.DATABASE_UPDATE_ROOT);
        }
        // 从新构建保存对象，控制写入数据
        SysRoleEntity newRole = new SysRoleEntity();
        newRole.setRoleId(roleId);
        newRole.setRoleCode(role.getRoleCode());
        newRole.setRoleName(role.getRoleName());
        newRole.setDescription(role.getDescription());
        newRole.setState(role.getState());
        newRole.setUpdateId(ShiroUtils.getUserId());
        //保存角色
        int roleCount = baseDao.update(newRole);
        //更新角色与权限关系
        int permCount = sysRolePermissionService.save(roleId, role.getPermIdList());
        int count = roleCount + permCount;
        if (count > 0) {
            //清空所有用户的权限缓存
            cleanPermCache();
        }
        return count;
    }

    /**
     * 删除角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(Integer[] roleIds) {
        //检测是否有用户在使用此角色
        for (Integer roleId : roleIds) {
            SysUserRoleEntity sysUserRole = new SysUserRoleEntity();
            sysUserRole.setRoleId(roleId);
            List<SysUserRoleEntity> list = sysUserRoleService.findList(sysUserRole);
            if (list.size() > 0) {
                throw new InfoException("角色 [ " + baseDao.findOne(roleId).getRoleName() + " ] 存在使用用户，不能被删除");
            }
        }

        //删除角色与权限关系
        int count = sysRolePermissionService.deleteByRoleIds(roleIds);
        //删除角色
        count += baseDao.deleteBatch(roleIds);
        if (count > 0) {
            //清空所有用户的权限缓存
            cleanPermCache();
        }
        return count;
    }

    /**
     * 清除所有用户的权限缓存
     */
    private void cleanPermCache() {
        Set<String> userStore = redisUtils.keys(RedisKeys.storeUser("*"));
        userStore.forEach(s -> {
            redisUtils.hdel(s, RedisKeys.USER_PERM);
        });
    }


}
