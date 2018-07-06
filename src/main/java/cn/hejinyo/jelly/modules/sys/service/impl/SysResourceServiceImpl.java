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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        int count = super.save(newResource);
        if (count > 0) {
            // 重排序
            changeSort(newResource, newResource.getSeq());
        }
        return count;
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
        Integer seq = sysResource.getSeq();


        SysResourceEntity newResource = new SysResourceEntity();
        newResource.setResId(resId);
        newResource.setType(sysResource.getType());
        newResource.setResCode(sysResource.getResCode());
        newResource.setResName(sysResource.getResName());
        newResource.setIcon(sysResource.getIcon());
        newResource.setSeq(sysResource.getSeq());
        newResource.setState(sysResource.getState());
        newResource.setCreateTime(sysResource.getCreateTime());
        int count = super.update(newResource);
        //如果资源修改了排序号
        if (seq != null && !seq.equals(oldResource.getSeq())) {
            count += changeSort(sysResource, seq);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer resId) {
        if (resId.equals(Constant.TREE_ROOT)) {
            //根节点不允许删除
            throw new InfoException(StatusCode.DATABASE_DELETE_ROOT);
        }

        SysResourceEntity sysResource = baseDao.findOne(resId);
        //查询是否还有子节点
        List<SysResourceEntity> list = baseDao.findListByParentId(resId);
        if (list.size() > 0) {
            throw new InfoException(StatusCode.DATABASE_DELETE_CHILD);
        }

        //查询资源下是否存在权限
        List<SysPermissionEntity> permList = sysPermissionService.findListByResId(resId);
        if (permList.size() > 0) {
            throw new InfoException("资源下存在权限");
        }
        //删除资源
        int count = baseDao.delete(resId);
        if (count > 0) {
            // 原来同级节点重排序
            reorder(sysResource.getParentId());
        }
        return count;
    }

    /**
     * 同级修改排序
     */
    private int changeSort(SysResourceEntity resource, Integer seq) {
        // 查询拖动节点所有同级节点
        List<SysResourceEntity> innerList = baseDao.findListByParentId(resource.getParentId());
        // 最大排序为同级长度
        innerList.removeIf(res -> res.getResId().equals(resource.getResId()));
        int innerSize = innerList.size();
        int innerSeq = 1;
        if (innerSize > 0) {
            int add = 1;
            for (int i = 0; i < innerSize; i++) {
                if (seq.equals(i + 1)) {
                    innerSeq += i;
                    // 拖动节点新位置后面的排序加1
                    add = 2;
                }
                innerList.get(i).setSeq(i + add);
            }
            if (seq > innerList.size()) {
                innerSeq = innerList.size() + 1;
            }
            // 修改同级节点所有排序
            baseDao.updateInnerAllSeq(innerList);
        }
        // 修改当前节点
        return baseDao.updateParentIdAndSeq(resource.getResId(), resource.getParentId(), innerSeq);
    }

    /**
     * 父节点资源重排序
     */
    private void reorder(Integer parentId, Integer... resIds) {
        // 查询拖动节点所有同级节点
        List<SysResourceEntity> currList = baseDao.findListByParentId(parentId);
        int currSize = currList.size();
        if (currSize > 0) {
            for (Integer resId : resIds) {
                // 去掉拖动节点在同级排序
                currList.removeIf(res -> res.getResId().equals(resId));
            }
            for (int i = 0; i < currSize; i++) {
                currList.get(i).setSeq(i + 1);
            }
            // 修改拖动节点原来位置所有排序
            baseDao.updateInnerAllSeq(currList);
        }

    }


    /**
     * 节点拖动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int nodeDrop(String location, Integer resId, Integer inResId) {
        // 拖动节点
        SysResourceEntity currRes = baseDao.findOne(resId);
        // 进入节点
        SysResourceEntity innerRes = baseDao.findOne(inResId);

        // 任何一个节点不存在，不进行操作
        if (currRes == null || innerRes == null) {
            return 0;
        }

        Integer currParentId = currRes.getParentId();
        Integer innerParentId = innerRes.getParentId();
        // 查询进入节点所有同级节点
        List<SysResourceEntity> innerList = baseDao.findListByParentId(innerParentId);
        //判断是不是同一个父节点
        boolean parentEquals = currParentId.equals(innerParentId);
        if (parentEquals) {
            // 去掉进入节点在同级排序
            innerList.removeIf(res -> res.getResId().equals(resId));
        }

        int innerSize = innerList.size();
        int innerSeq = 1;
        int add = 1;
        int count;
        switch (location) {
            // 进入节点及其后面的依次加1，被拖拽节点排序号设置为进入节点
            case "before":
                for (int i = 0; i < innerSize; i++) {
                    if (innerList.get(i).getResId().equals(inResId)) {
                        innerSeq += i;
                        // 拖动节点新位置后面的排序加1
                        add = 2;
                    }
                    innerList.get(i).setSeq(i + add);
                }
                // 修改进入节点所有排序
                baseDao.updateInnerAllSeq(innerList);
                // 修改拖动节点
                count = baseDao.updateParentIdAndSeq(resId, innerParentId, innerSeq);
                break;
            case "after":
                for (int i = 0; i < innerSize; i++) {
                    innerList.get(i).setSeq(i + add);
                    if (innerList.get(i).getResId().equals(inResId)) {
                        innerSeq += i;
                        add = 2;
                    }
                }
                // 修改进入节点所有排序
                baseDao.updateInnerAllSeq(innerList);
                // 修改拖动节点
                count = baseDao.updateParentIdAndSeq(resId, innerParentId, innerSeq + 1);
                break;
            case "inner":
                // 被拖拽节点父节点被进入节点，序号为进入节点的子节点长度+1
                count = baseDao.updateParentIdAndSeq(resId, inResId, baseDao.findListByParentId(inResId).size() + 1);
                break;
            default:
                return 0;
        }

        // 不同父节点，更新拖动节点原来排序
        if (!parentEquals) {
            reorder(currParentId, resId);
        }
        return count;
    }

}
