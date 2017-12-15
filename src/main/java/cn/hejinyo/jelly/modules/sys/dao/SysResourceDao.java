package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysResource;
import cn.hejinyo.jelly.modules.sys.model.dto.ResourceTreeDTO;
import cn.hejinyo.jelly.modules.sys.model.dto.UserMenuDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysResourceDao extends BaseDao<SysResource, Integer> {

    /**
     * 查询用户编号可用菜单
     *
     * @param userId
     */
    List<UserMenuDTO> getUserMenuList(int userId);

    /**
     * 递归获得所有资源树
     */
    List<ResourceTreeDTO> getRecursionTree();

    /**
     * 序号加操作
     *
     * @param sysResource
     */
    int updateAdditionSeq(SysResource sysResource);

    /**
     * 序号减操作
     *
     * @param sysResource
     */
    int updateSubtractionSeq(SysResource sysResource);
}