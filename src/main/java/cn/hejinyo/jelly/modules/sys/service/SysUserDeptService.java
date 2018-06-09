package cn.hejinyo.jelly.modules.sys.service;


import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysDeptEntity;
import cn.hejinyo.jelly.modules.sys.model.SysUserDeptEntity;

import java.util.List;

/**
 * 人员部门业务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:21
 */
public interface SysUserDeptService extends BaseService<SysUserDeptEntity, Integer> {
    /**
     * 查询用户部门列表
     */
    List<SysUserDeptEntity> getUserDeptList(Integer userId);

    /**
     * 保存用户部门关系
     */
    int save(Integer userId, List<SysUserDeptEntity> userDeptList);

    /**
     * 查询用户部门Id列表
     */
    List<Integer> getUserDeptId(Integer userId);

    /**
     * 获取用户所有的部门，包含子部门
     */
    List<Integer> getUserAllDeptId(Integer userId);

    /**
     * 递归获取用户部门和子部门
     *
     * @param isRoot       是否显示根节点
     * @param list         需要遍历的列表
     * @param parentIdList 父节点编号列表
     */
    List<Integer> recursionDept(boolean isRoot, List<SysDeptEntity> list, List<Integer> parentIdList);

    /**
     * 删除用户部门关系
     */
    int deleteByUserId(Integer userId);

    /**
     * 根据用户ID，获取部门列表
     */
    List<SysDeptEntity> getDeptList(Integer userId);

    /**
     * 查询用户部门列表,包含部门信息
     */
    List<SysUserDeptEntity> getUserDeptInfoList(Integer userId);

    /**
     * 根绝部门编号查询用户编号列表
     */
    List<Integer> getUserIdByDeptId(Integer deptId);

    /**
     * 删除人员与部门关系
     */
    int deleteByDeptIds(Integer[] deptIds);

}
