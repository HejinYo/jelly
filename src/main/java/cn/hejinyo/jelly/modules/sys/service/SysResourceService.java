package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysResource;
import cn.hejinyo.jelly.modules.sys.model.dto.ResourceTreeDTO;
import cn.hejinyo.jelly.modules.sys.model.dto.UserMenuDTO;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/22 15:10
 */
public interface SysResourceService extends BaseService<SysResource, Integer> {

    /**
     * 查询用户编号可用菜单列表
     *
     * @param userId
     */
    List<UserMenuDTO> getUserMenuList(int userId);

    /**
     * 查询用户编号可用菜单树
     */
    List<UserMenuDTO> getUserMenuTree(int userId);

    /**
     * 递归获得所有资源树
     */
    List<ResourceTreeDTO> getRecursionTree();

    /**
     * 资源编码是否存在
     *
     * @param resCode
     */
    boolean isExistResCode(String resCode);


}
