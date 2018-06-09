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
     * 获取所有部门列表
     */
    List<SysDeptEntity> getAllDeptList();

    /**
     * 获取所有部门Id列表
     */
    List<Integer> getAllDeptIdList();

    /**
     * 部门管理树数据
     */
    HashMap<String, List<SysDeptEntity>> getRecursionTree(boolean showRoot);

    /**
     * 部门选择数据,排除状态为不为正常的组织
     */
    HashMap<String, List<SysDeptEntity>> getSelectTree(boolean showRoot);

    /**
     * 检测是否越权，增加和修改的部门是否在当前用户的部门之下
     */
    boolean checkPermission(Integer deptId);
}
