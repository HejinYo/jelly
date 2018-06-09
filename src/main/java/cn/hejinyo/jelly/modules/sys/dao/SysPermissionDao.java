package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysPermissionEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * sys_permission 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/09 16:47
 */
@Mapper
public interface SysPermissionDao extends BaseDao<SysPermissionEntity, Integer> {
    /**
     * 查找角色编号对应的权限编码字符串
     */
    Set<String> getRolePermissionSet(int roleId);

    /**
     * 查询指定resPid的资源
     */
    List<RolePermissionTreeDTO> findAllResourceList();

    /**
     * 查询指定resCode的权限
     */
    List<RolePermissionTreeDTO> findAllPermissionList();

    /**
     * 删除资源对应权限数据
     */
    int deletePermByResCode(String resCode);

    /**
     * 根据resId更新resCode
     */
    int updateResCodeByResId(SysPermissionEntity permission);
}