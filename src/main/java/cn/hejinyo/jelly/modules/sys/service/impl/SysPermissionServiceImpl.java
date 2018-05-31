package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.dao.SysPermissionDao;
import cn.hejinyo.jelly.modules.sys.model.SysPermission;
import cn.hejinyo.jelly.modules.sys.model.SysResource;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;
import cn.hejinyo.jelly.modules.sys.service.SysPermissionService;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import cn.hejinyo.jelly.modules.sys.service.SysRoleResourceService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:06
 */
@Service
public class SysPermissionServiceImpl extends BaseServiceImpl<SysPermissionDao, SysPermission, Integer> implements SysPermissionService {

    @Autowired
    private SysResourceService sysResourceService;
    @Autowired
    private SysRoleResourceService sysRoleResourceService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean isExist(SysPermission sysPermission) {
        SysPermission permission = new SysPermission();
        permission.setPermCode(sysPermission.getPermCode());
        permission.setResCode(sysPermission.getResCode());
        permission.setResId(sysPermission.getResId());
        return baseDao.exsit(permission);
    }

    @Override
    public int save(SysPermission sysPermission) {
        SysResource sysResource = sysResourceService.findOne(sysPermission.getResId());
        if (sysResource == null) {
            throw new InfoException("资源不存在");
        }
        sysPermission.setResCode(sysResource.getResCode());
        sysPermission.setCreateTime(new Date());
        sysPermission.setCreateId(ShiroUtils.getUserId());
        //清除redis中的权限缓存
        redisUtils.cleanKey(RedisKeys.getAuthCacheKey("*"));
        return baseDao.save(sysPermission);
    }

    @Override
    public Set<String> getRolePermissionSet(int roleId) {
        return baseDao.getRolePermissionSet(roleId);
    }

    /**
     * 递归获得资源权限树
     */
    @Override
    public List<RolePermissionTreeDTO> getResourcePermissionTree() {
        List<RolePermissionTreeDTO> resourceList = new CopyOnWriteArrayList<>(baseDao.findAllResourceList());
        List<RolePermissionTreeDTO> permissionList = new CopyOnWriteArrayList<>(baseDao.findAllPermissionList());
        return recursionRes(0, resourceList, permissionList);
    }

    @Override
    public int update(SysPermission sysPermission) {
        SysPermission oldPermission = baseDao.findOne(sysPermission.getPermId());
        if (oldPermission == null) {
            throw new InfoException("权限不存在");
        }
        if (!oldPermission.getPermCode().equals(sysPermission.getPermCode()) || !oldPermission.getResId().equals(sysPermission.getResId())) {
            if (isExist(sysPermission)) {
                throw new InfoException("资源权限已经存在");
            }
        }
        SysResource sysResource = sysResourceService.findOne(sysPermission.getResId());
        if (sysResource == null) {
            throw new InfoException("资源不存在");
        }
        sysPermission.setResCode(sysResource.getResCode());
        //清除redis中的权限缓存
        redisUtils.cleanKey(RedisKeys.getAuthCacheKey("*"));
        return super.update(sysPermission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer permId) {
        //删除角色资源表数据
        sysRoleResourceService.deleteRolePrem(permId);
        //清除redis中的权限缓存
        redisUtils.cleanKey(RedisKeys.getAuthCacheKey("*"));
        return baseDao.delete(permId);
    }

    /**
     * 删除资源对应权限数据
     */
    @Override
    public int deletePermByResCode(String resCode) {
        //清除redis中的权限缓存
        redisUtils.cleanKey(RedisKeys.getAuthCacheKey("*"));
        return baseDao.deletePermByResCode(resCode);
    }

    /**
     * 根据resId更新resCode
     */
    @Override
    public int updateResCodeByResId(SysPermission permission) {
        //清除redis中的权限缓存
        redisUtils.cleanKey(RedisKeys.getAuthCacheKey("*"));
        return baseDao.updateResCodeByResId(permission);
    }


    /**
     * 递归生成授权树
     */
    private List<RolePermissionTreeDTO> recursionRes(Integer parentId, List<RolePermissionTreeDTO> resList, List<RolePermissionTreeDTO> permList) {
        List<RolePermissionTreeDTO> result = new ArrayList<>();
        resList.forEach(value -> {
            if (parentId.equals(value.getResPid())) {
                List<RolePermissionTreeDTO> child = recursionRes(value.getResId(), resList, permList);
                List<RolePermissionTreeDTO> childPerm = recursionPerm(value.getResId(), permList);
                if (childPerm.size() > 0) {
                    child.addAll(0, childPerm);
                }
                value.setChildren(child);
                result.add(value);
                resList.remove(value);
            }
        });
        return result;
    }

    private List<RolePermissionTreeDTO> recursionPerm(Integer parentId, List<RolePermissionTreeDTO> list) {
        List<RolePermissionTreeDTO> result = new ArrayList<>();
        list.forEach(value -> {
            if (parentId.equals(value.getResId())) {
                result.add(value);
                list.remove(value);
            }
        });
        return result;
    }

}
