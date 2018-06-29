package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysResourceEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.RoutersMenuDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sys_resource 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/09 16:46
 */
@Mapper
public interface SysResourceDao extends BaseDao<SysResourceEntity, Integer> {
    /**
     * 查询所有菜单列表
     */
    List<RoutersMenuDTO> findAllMenuList();

    /**
     * 查询用户菜单列表
     */
    List<RoutersMenuDTO> findUserMenuList(@Param("userId") Integer userId);

    /**
     * 查询所有资源列表
     */
    List<SysResourceEntity> findAllResourceList();

    /**
     * 获取系统所有有效资源列表，状态正常
     */
    List<SysResourceEntity> findValidResourceList();


}