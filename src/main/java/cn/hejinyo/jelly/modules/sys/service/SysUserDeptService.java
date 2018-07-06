package cn.hejinyo.jelly.modules.sys.service;


import cn.hejinyo.jelly.common.base.BaseService;
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
     * 用户所在部门Id列表，带缓存
     */
    List<Integer> getCurDeptIdListByUserId(Integer userId);

    /**
     * 用户所在部门和子部门，带缓存
     */
    List<Integer> getAllDeptIdListByUserId(Integer userId);

    /**
     * 用户拥有的子部门，带缓存
     */
    List<Integer> getSubDeptIdListByUserId(Integer userId);

    /**
     * 根据用户编号，删除与此用户的部门关系
     */
    int deleteByUserId(Integer userId);

    /**
     * 根据部门编号，删除与此部门的用户关系
     */
    int deleteByDeptId(Integer deptId);

    /**
     * 保存用户部门关系
     */
    int save(Integer userId, List<Integer> deptList);

    /**
     * 根绝部门编号查询用户编号列表
     */
    List<Integer> getUserIdByDeptId(Integer deptId);

}
