package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysDeptEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sys_dept 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/09 18:31
 */
@Mapper
public interface SysDeptDao extends BaseDao<SysDeptEntity, Integer> {

    /**
     * 获取所有部门列表
     */
    List<SysDeptEntity> findAllDeptList();

    /**
     * 有效部门数据列表
     */
    List<SysDeptEntity> findValidDeptList();

    /**
     * 查询子部门
     */
    List<SysDeptEntity> findDeptByParentIds(Integer[] deptIds);

}