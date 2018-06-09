package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.RecursionUtil;
import cn.hejinyo.jelly.modules.sys.dao.SysResourceDao;
import cn.hejinyo.jelly.modules.sys.model.SysPermissionEntity;
import cn.hejinyo.jelly.modules.sys.model.SysResourceEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.UserMenuDTO;
import cn.hejinyo.jelly.modules.sys.service.SysPermissionService;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import cn.hejinyo.jelly.modules.sys.service.SysRoleResourceService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/22 15:11
 */
@Service
public class SysResourceServiceImpl extends BaseServiceImpl<SysResourceDao, SysResourceEntity, Integer> implements SysResourceService {

    @Autowired
    private SysRoleResourceService sysRoleResourceService;

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 查询用户编号可用菜单列表
     */
    @Override
    public List<UserMenuDTO> getUserMenuList(int userId) {
        if (userId == Constant.SUPER_ADMIN) {
            //系统管理员，所有有效菜单
            return baseDao.findAllMenuList();
        }
        return baseDao.findUserMenuList(userId);
    }

    /**
     * 查询用户编号可用菜单树
     */
    @Override
    public List<UserMenuDTO> getUserMenuTree(int userId) {
        //递归生成用户菜单树
        return RecursionUtil.tree(false, UserMenuDTO.class, "getResId",
                new CopyOnWriteArrayList<>(getUserMenuList(userId)), Collections.singletonList(Constant.TREE_ROOT));
    }

    /**
     * 递归获得所有资源树
     */
    @Override
    public HashMap<String, List<SysResourceEntity>> getRecursionTree() {
        return RecursionUtil.listTree(true, SysResourceEntity.class, "getResId", baseDao.findAllResourceList(), Collections.singletonList(Constant.TREE_ROOT));
    }


    @Override
    public boolean isExistResCode(String resCode) {
        //查询resCode是否存在
        SysResourceEntity sysResource = new SysResourceEntity();
        sysResource.setResCode(resCode);
        return baseDao.exsit(sysResource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(SysResourceEntity sysResource) {
        SysResourceEntity newResource = new SysResourceEntity();
        baseDao.updateAdditionSeq(sysResource);
        newResource.setParentId(sysResource.getParentId());
        newResource.setType(sysResource.getType());
        newResource.setResName(sysResource.getResName());
        newResource.setResCode(sysResource.getResCode());
        newResource.setIcon(sysResource.getIcon());
        newResource.setCreateTime(new Date());
        newResource.setSeq(sysResource.getSeq());
        newResource.setCreateId(ShiroUtils.getUserId());
        newResource.setState(sysResource.getState());
        return super.save(newResource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(SysResourceEntity sysResource) {
        int resid = sysResource.getResId();
        int resPid = sysResource.getParentId();
        SysResourceEntity oldResource = findOne(resid);
        if (resPid == resid) {
            throw new InfoException("不能选择自己作为上级资源");
        }
        if (null == oldResource) {
            throw new InfoException("资源不存在");
        }
        SysResourceEntity newResource = new SysResourceEntity();

        newResource.setResId(resid);
        newResource.setType(sysResource.getType());
        newResource.setResCode(sysResource.getResCode());
        newResource.setResName(sysResource.getResName());
        newResource.setParentId(resPid);
        newResource.setIcon(sysResource.getIcon());
        newResource.setSeq(sysResource.getSeq());
        newResource.setState(sysResource.getState());
        newResource.setCreateTime(sysResource.getCreateTime());
        if (resPid != oldResource.getParentId()) {
            //上级资源改变，原上级资源seq减修改
            baseDao.updateSubtractionSeq(oldResource);
            //新的上级资源seq加修改
            baseDao.updateAdditionSeq(newResource);
        } else {
            if (!sysResource.getSeq().equals(oldResource.getSeq())) {
                //原上级资源seq减修改
                baseDao.updateSubtractionSeq(oldResource);
                //原上级资源seq加修改
                baseDao.updateAdditionSeq(newResource);
            }
        }

        int count = super.update(newResource);
        //资源编码改变，同步修改权限表的resCode
        if (count > 0 && !sysResource.getResCode().equals(oldResource.getResCode())) {
            SysPermissionEntity permission = new SysPermissionEntity();
            permission.setResId(newResource.getResId());
            sysPermissionService.updateResCodeByResId(permission);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer resId) {
        SysResourceEntity sysResource = baseDao.findOne(resId);
        if (sysResource == null) {
            throw new InfoException("资源ID [" + resId + "] 不存在");
        }

        //查询是否还有子节点
        SysResourceEntity sysRes = new SysResourceEntity();
        sysRes.setParentId(resId);
        List<SysResourceEntity> list = baseDao.findList(sysRes);
        if (list.size() > 0) {
            throw new InfoException("资源 [" + sysResource.getResName() + "] 存在子节点");
        }

        //删除角色资源表数据
        sysRoleResourceService.deleteRoleRes(resId);

        //删除资源对应权限数据
        sysPermissionService.deletePermByResCode(sysResource.getResCode());

        //删除资源
        int result = baseDao.delete(resId);
        if (result > 0) {
            baseDao.updateSubtractionSeq(sysResource);
        }
        return result;
    }

}
