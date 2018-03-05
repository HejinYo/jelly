package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.modules.sys.dao.SysUserRoleDao;
import cn.hejinyo.jelly.modules.sys.model.SysUserRole;
import cn.hejinyo.jelly.modules.sys.service.SysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/3/3 19:11
 */
@Service
public class SysUserRoleServiceImpl extends BaseServiceImpl<SysUserRoleDao, SysUserRole, Integer> implements SysUserRoleService {

    /**
     * 根据用户id删除用户角色关系
     *
     * @param ids userId数组
     */
    @Override
    public int deleteListByUserId(Integer[] ids) {
        return baseDao.deleteListByUserId(ids);
    }
}
