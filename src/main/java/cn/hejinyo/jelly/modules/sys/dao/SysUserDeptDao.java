package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysDeptEntity;
import cn.hejinyo.jelly.modules.sys.model.SysUserDeptEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sys_user_dept 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/09 18:34
 */
@Mapper
public interface SysUserDeptDao extends BaseDao<SysUserDeptEntity, Integer> {
    /**
     * 查询用户部门ID列表
     */
    List<Integer> findDeptIdListByUserId(Integer userId);

    /**
     * 查询用户部门关系列表
     */
    List<SysUserDeptEntity> findDeptListByUserId(Integer userId);


    /**
     * 删除用户部门关系
     */
    int deleteByUserId(Integer userId);

    /**
     * 根据用户ID，获取部门列表
     */
    List<SysDeptEntity> findDeptList(Integer userId);

    /**
     * 查询用户部门列表,包含部门信息
     */
    List<SysUserDeptEntity> findUserDeptInfoList(Integer userId);

    /**
     * 根绝角色编号查询用户编号列表
     */
    List<Integer> findUserIdByDeptId(Integer deptId);

    /**
     * 根据部门编号批量删除用户部门关系
     */
    int deleteByDeptIds(Integer[] deptIds);
}