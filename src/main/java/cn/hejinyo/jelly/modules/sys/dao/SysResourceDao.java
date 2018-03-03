package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysResource;
import cn.hejinyo.jelly.modules.sys.model.dto.ResourceTreeDTO;
import cn.hejinyo.jelly.modules.sys.model.dto.UserMenuDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 16:54
 */
@Mapper
public interface SysResourceDao extends BaseDao<SysResource, Integer> {

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

    /**
     * 查询用户所有授权菜单列表
     */
    List<UserMenuDTO> findAllMenuList(@Param("userId") Integer userId);
}