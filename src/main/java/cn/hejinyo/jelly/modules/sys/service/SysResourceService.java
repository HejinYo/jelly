package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysResourceEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.UserMenuDTO;

import java.util.HashMap;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/22 15:10
 */
public interface SysResourceService extends BaseService<SysResourceEntity, Integer> {

    /**
     * 查询用户编号可用菜单列表
     */
    List<UserMenuDTO> getUserMenuList(int userId);

    /**
     * 查询用户编号可用菜单树
     */
    List<UserMenuDTO> getUserMenuTree(int userId);

    /**
     * 获取系统所有资源列表
     */
    List<SysResourceEntity> getAllResourceList();

    /**
     * 获取系统所有有效资源列表，状态正常
     */
    List<SysResourceEntity> getValidResourceList();

    /**
     * 资源树数据
     */
    HashMap<String, List<SysResourceEntity>> getResourceListTree(boolean valid, boolean showRoot);

    /**
     * 指定一个节点，在系统所有资源中 递归遍历  指定节点开始 的所有子节点 为列表
     *
     * @param isRoot       是否显示根节点
     * @param parentIdList 父节点编号列表
     */
    List<Integer> recursionResource(boolean isRoot, List<Integer> parentIdList);

}
