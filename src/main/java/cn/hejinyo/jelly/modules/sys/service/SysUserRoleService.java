package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysUserRole;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
public interface SysUserRoleService extends BaseService<SysUserRole, Integer> {

    /**
     * 根据用户id删除用户角色关系
     *
     * @param ids userId数组
     */
    int deleteListByUserId(Integer[] ids);
}
