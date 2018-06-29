package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.RecursionUtil;
import cn.hejinyo.jelly.modules.sys.dao.SysResourceDao;
import cn.hejinyo.jelly.modules.sys.model.SysPermissionEntity;
import cn.hejinyo.jelly.modules.sys.model.SysResourceEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.RoutersMenuDTO;
import cn.hejinyo.jelly.modules.sys.service.SysPermissionService;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/22 15:11
 */
@Service
public class SysResourceServiceImpl extends BaseServiceImpl<SysResourceDao, SysResourceEntity, Integer> implements SysResourceService {
    /**
     * 递归资源获取id的名称
     */
    private static final String GET_ID_NAME = "getResId";

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 查询用户编号可用菜单列表
     */
    @Override
    public List<RoutersMenuDTO> getUserMenuList(int userId) {
        if (Constant.SUPER_ADMIN.equals(userId)) {
            //系统管理员，所有有效菜单
            return baseDao.findAllMenuList();
        }
        return baseDao.findUserMenuList(userId);
    }

    /**
     * 查询用户编号可用菜单树
     */
    @Override
    public List<RoutersMenuDTO> getUserMenuTree(int userId) {
        //递归生成用户菜单树
        return RecursionUtil.tree(false, RoutersMenuDTO.class, GET_ID_NAME, new CopyOnWriteArrayList<>(getUserMenuList(userId)), Collections.singletonList(Constant.TREE_ROOT));
    }

    /**
     * 获取系统所有资源列表
     */
    @Override
    public List<SysResourceEntity> getAllResourceList() {
        return baseDao.findAllResourceList();
    }

    /**
     * 获取系统所有有效资源列表，状态正常
     */
    @Override
    public List<SysResourceEntity> getValidResourceList() {
        return baseDao.findValidResourceList();
    }

    /**
     * 资源树数据
     */
    @Override
    public HashMap<String, List<SysResourceEntity>> getResourceListTree(boolean valid, boolean showRoot) {
        List<SysResourceEntity> list = valid ? getValidResourceList() : getAllResourceList();
        return RecursionUtil.listTree(showRoot, SysResourceEntity.class, GET_ID_NAME, list, Collections.singletonList(Constant.TREE_ROOT));
    }

    /**
     * 保存资源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(SysResourceEntity sysResource) {
        SysResourceEntity newResource = new SysResourceEntity();
        newResource.setCreateId(ShiroUtils.getUserId());
        newResource.setParentId(sysResource.getParentId());
        newResource.setType(sysResource.getType());
        newResource.setResName(sysResource.getResName());
        newResource.setResCode(sysResource.getResCode());
        newResource.setIcon(sysResource.getIcon());
        newResource.setSeq(sysResource.getSeq());
        newResource.setState(sysResource.getState());
        return super.save(newResource);
    }

    /**
     * 指定一个节点，在系统所有资源中 递归遍历  指定节点开始 的所有子节点 为列表
     *
     * @param isRoot       是否显示根节点
     * @param parentIdList 父节点编号列表
     */
    @Override
    public List<Integer> recursionResource(boolean isRoot, List<Integer> parentIdList) {
        List<Integer> allDeptList = new ArrayList<>();
        // 遍历系统所有资源
        List<SysResourceEntity> list = getAllResourceList();
        RecursionUtil.list(allDeptList, SysResourceEntity.class, GET_ID_NAME, isRoot, new CopyOnWriteArrayList<>(list), parentIdList);
        return allDeptList;
    }

    /**
     * 修改资源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Integer resId, SysResourceEntity sysResource) {
        if (Constant.TREE_ROOT.equals(resId)) {
            // 根节点不能修改
            throw new InfoException(StatusCode.DATABASE_UPDATE_ROOT);
        }

        // 资源原信息
        SysResourceEntity oldResource = baseDao.findOne(resId);
        Integer parentId = sysResource.getParentId();

        //如果资源修改了父节点，需要检测新的父节点是否是当前节点的子节点，如果是，会造成递归死循环
        if (parentId != null && !parentId.equals(oldResource.getParentId())) {
            if (baseDao.findOne(parentId) == null) {
                // 检查父节点是否存在
                throw new InfoException(StatusCode.DATABASE_NO_FATHER);
            }
            // 递归获取当前节点的所有子节点
            List<Integer> allIdList = recursionResource(true, Collections.singletonList(resId));
            //检查新的父节点是否在子节点列表内
            if (allIdList.contains(parentId)) {
                throw new InfoException(StatusCode.DATABASE_UPDATE_LOOP);
            }
        }

        SysResourceEntity newResource = new SysResourceEntity();
        newResource.setResId(resId);
        newResource.setType(sysResource.getType());
        newResource.setResCode(sysResource.getResCode());
        newResource.setResName(sysResource.getResName());
        newResource.setParentId(parentId);
        newResource.setIcon(sysResource.getIcon());
        newResource.setSeq(sysResource.getSeq());
        newResource.setState(sysResource.getState());
        newResource.setCreateTime(sysResource.getCreateTime());
        return super.update(newResource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer resId) {
        if (resId.equals(Constant.TREE_ROOT)) {
            //根节点不允许删除
            throw new InfoException(StatusCode.DATABASE_DELETE_ROOT);
        }
        //查询是否还有子节点
        SysResourceEntity sysRes = new SysResourceEntity();
        sysRes.setParentId(resId);
        List<SysResourceEntity> list = baseDao.findList(sysRes);
        if (list.size() > 0) {
            throw new InfoException(StatusCode.DATABASE_DELETE_CHILD);
        }

        //查询资源下是否存在权限
        SysPermissionEntity sysPermission = new SysPermissionEntity();
        sysPermission.setResId(resId);
        sysPermissionService.findList(sysPermission);
        if (list.size() > 0) {
            throw new InfoException("资源下存在权限");
        }
        //删除资源
        return baseDao.delete(resId);
    }

}
