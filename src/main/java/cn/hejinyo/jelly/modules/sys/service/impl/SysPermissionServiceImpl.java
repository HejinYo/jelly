package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.dao.SysPermissionDao;
import cn.hejinyo.jelly.modules.sys.model.SysPermissionEntity;
import cn.hejinyo.jelly.modules.sys.model.SysResourceEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.AuthTreeDTO;
import cn.hejinyo.jelly.modules.sys.service.SysPermissionService;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import cn.hejinyo.jelly.modules.sys.service.SysRolePermissionService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:06
 */
@Service
public class SysPermissionServiceImpl extends BaseServiceImpl<SysPermissionDao, SysPermissionEntity, Integer> implements SysPermissionService {

    @Autowired
    private SysResourceService sysResourceService;

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 查询所有有效的权限List
     */
    @Override
    public List<SysPermissionEntity> getAllPermissionList() {
        return baseDao.getAllPermissionList();
    }

    /**
     * 递归获得授权树
     */
    @Override
    public List<AuthTreeDTO> getAuthTree() {
        List<SysResourceEntity> resourceList = new CopyOnWriteArrayList<>(sysResourceService.getAllResourceList());
        List<SysPermissionEntity> permissionList = new CopyOnWriteArrayList<>(getAllPermissionList());
        return recursionAuthTree(Constant.TREE_ROOT, resourceList, permissionList);
    }

    private List<AuthTreeDTO> recursionAuthTree(Integer parentId, List<SysResourceEntity> resList, List<SysPermissionEntity> permList) {
        List<AuthTreeDTO> result = new ArrayList<>();
        // 开始遍历资源
        resList.forEach(res -> {
            // 资源ID 等于父资源ID
            if (parentId.equals(res.getResId())) {
                // 获取此资源的子资源
                List<AuthTreeDTO> child = recursionAuthTree(res.getResId(), resList, permList);
                // 获取此资源的权限
                List<AuthTreeDTO> childPerm = forEachPerm(res.getResId(), permList);
                //子资源添加到权限的后面
                childPerm.addAll(child);
                AuthTreeDTO authTreeDTO = new AuthTreeDTO("2:" + res.getResId(), res.getResName(), 0, childPerm);
                result.add(authTreeDTO);
                resList.remove(res);
            }
        });
        return result;
    }

    /**
     * 获取资源的权限列表
     */
    private List<AuthTreeDTO> forEachPerm(Integer parentId, List<SysPermissionEntity> list) {
        List<AuthTreeDTO> result = new ArrayList<>();
        list.forEach(perm -> {
            if (parentId.equals(perm.getResId())) {
                AuthTreeDTO authTreeDTO = new AuthTreeDTO("1:" + perm.getResId(), perm.getPermName(), perm.getPermId(), 1);
                result.add(authTreeDTO);
                list.remove(perm);
            }
        });
        return result;
    }

    /**
     * 保存权限
     */
    @Override
    public int save(SysPermissionEntity sysPermission) {
        // 从新构建保存对象，控制写入数据
        SysPermissionEntity newPermission = new SysPermissionEntity();
        newPermission.setPermName(sysPermission.getPermName());
        newPermission.setPermCode(sysPermission.getPermCode());
        newPermission.setResId(sysPermission.getResId());
        newPermission.setState(sysPermission.getState());
        newPermission.setCreateId(ShiroUtils.getUserId());
        int count = baseDao.save(newPermission);
        if (count > 0) {
            //清除redis中的权限缓存
            cleanPermCache();
        }
        return count;
    }

    /**
     * 更新权限
     */
    @Override
    public int update(Integer permId, SysPermissionEntity sysPermission) {
        // 从新构建保存对象，控制写入数据
        SysPermissionEntity newPermission = new SysPermissionEntity();
        newPermission.setPermId(permId);
        newPermission.setPermName(sysPermission.getPermName());
        newPermission.setPermCode(sysPermission.getPermCode());
        newPermission.setResId(sysPermission.getResId());
        newPermission.setState(sysPermission.getState());
        //保存权限
        int count = baseDao.update(newPermission);
        //更新权限与权限关系
        if (count > 0) {
            //清空所有用户的权限缓存
            cleanPermCache();
        }
        return count;
    }

    /**
     * 删除权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer permId) {
        int count = baseDao.delete(permId);
        if (count > 0) {
            //删除角色权限表数据
            sysRolePermissionService.deleteByPermId(permId);
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

    /**
     * 根据资源编号查询所有权限
     */
    @Override
    public List<SysPermissionEntity> findListByResId(Integer resId) {
        return baseDao.findListByResId(resId);
    }
}
