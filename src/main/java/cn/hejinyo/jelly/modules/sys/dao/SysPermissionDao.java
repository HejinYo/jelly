package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysPermissionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sys_permission 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/09 16:47
 */
@Mapper
public interface SysPermissionDao extends BaseDao<SysPermissionEntity, Integer> {

    /**
     * 查询所有有效的权限List
     */
    List<SysPermissionEntity> getAllPermissionList();

    /**
     * 根据资源编号查询所有权限
     */
    List<SysPermissionEntity> findListByResId(Integer resId);
}