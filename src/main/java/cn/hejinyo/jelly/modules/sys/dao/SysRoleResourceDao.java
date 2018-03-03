package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysRoleResource;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

/**
 * sys_role_resource 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/03/03 12:59
 */
@Mapper
public interface SysRoleResourceDao extends BaseDao<SysRoleResource, Integer> {

    /**
     * 保存角色权限
     */
    int saveRolePermission(HashMap<String, Object> param);
}