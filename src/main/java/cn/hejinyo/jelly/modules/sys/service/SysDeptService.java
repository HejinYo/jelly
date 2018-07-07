package cn.hejinyo.jelly.modules.sys.service;


import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysDeptEntity;

import java.util.HashMap;
import java.util.List;

/**
 * 部门业务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:21
 */
public interface SysDeptService extends BaseService<SysDeptEntity, Integer> {

    /**
     * 根据部门Id列表获取部门信息列表
     */
    List<SysDeptEntity> getListByDeptIdList(List<Integer> deptIdList);

    /**
     * 获取系统所有部门列表
     */
    List<SysDeptEntity> getAllDeptList();

    /**
     * 获取系统所有有效部门列表，状态正常
     */
    List<SysDeptEntity> getValidDeptList();

    /**
     * 指定一个节点，在系统所有部门中 递归遍历  指定节点开始 的所有子节点 为列表
     *
     * @param isRoot       是否显示根节点
     * @param parentIdList 父节点编号列表
     */
    List<Integer> recursionDept(boolean isRoot, List<Integer> parentIdList);

    /**
     * 部门树数据
     */
    HashMap<String, List<SysDeptEntity>> getDeptListTree(boolean valid, boolean showRoot);

    /**
     * 检测是否越权，增加和修改的部门是否在当前用户的部门之下
     *
     * @param showSelf 是否包含用户所在部门
     * @param deptId   需要检测的部门ID
     */
    boolean checkPermission(Boolean showSelf, Integer deptId);

    /**
     * 节点拖动
     */
    int nodeDrop(String location, Integer deptId, Integer inDeptId);
}
