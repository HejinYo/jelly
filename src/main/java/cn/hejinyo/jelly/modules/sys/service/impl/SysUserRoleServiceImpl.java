package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.modules.sys.dao.SysUserRoleDao;
import cn.hejinyo.jelly.modules.sys.model.SysUserRoleEntity;
import cn.hejinyo.jelly.modules.sys.service.SysUserRoleService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/3/3 19:11
 */
@Service
public class SysUserRoleServiceImpl extends BaseServiceImpl<SysUserRoleDao, SysUserRoleEntity, Integer> implements SysUserRoleService {


    /**
     * 根据用户拥有的角色ID列表
     */
    @Override
    public List<Integer> getRoleIdListByUserId(Integer userId) {
        return baseDao.findRoleIdListByUserId(userId);
    }

    /**
     * 保存用户角色关系
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(Integer userId, List<Integer> roleIdList) {
        //先删除用户角色关系
        int count = baseDao.deleteByUserId(userId);
        if (roleIdList.size() == 0) {
            return count;
        }
        //保存角色与用户关系
        List<SysUserRoleEntity> userRoleList = new ArrayList<>();
        for (Integer roleId : roleIdList) {
            SysUserRoleEntity userRole = new SysUserRoleEntity();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateId(ShiroUtils.getUserId());
            userRoleList.add(userRole);
        }

        return baseDao.saveBatch(userRoleList);
    }

    /**
     * 删除用户与角色关系
     */
    @Override
    public int deleteByUserId(Integer userId) {
        return baseDao.deleteByUserId(userId);
    }
}
