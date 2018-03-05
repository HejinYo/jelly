package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.modules.sys.dao.SysResourceDao;
import cn.hejinyo.jelly.modules.sys.model.SysResource;
import cn.hejinyo.jelly.modules.sys.model.dto.UserMenuDTO;
import cn.hejinyo.jelly.modules.sys.service.SysPermissionService;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import cn.hejinyo.jelly.modules.sys.service.SysRoleResourceService;
import cn.hejinyo.jelly.modules.sys.utils.ShiroUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/4/22 15:11
 */
@Service
public class SysResourceServiceImpl extends BaseServiceImpl<SysResourceDao, SysResource, Integer> implements SysResourceService {

    private static final Logger logger = LoggerFactory.getLogger(SysResourceServiceImpl.class);

    @Autowired
    private SysRoleResourceService sysRoleResourceService;

    @Autowired
    private SysPermissionService sysPermissionService;

    @Override
    public List<UserMenuDTO> getUserMenuList(int userId) {
        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN) {
            return baseDao.findAllMenuList(null);
        }
        return baseDao.findAllMenuList(userId);
    }

    @Override
    public List<UserMenuDTO> getUserMenuTree(int userId) {
        //用户菜单树
        return recursionMenu(0, new CopyOnWriteArrayList<>(getUserMenuList(userId)));
    }

    /**
     * 将菜单列表递归成树
     */
    private List<UserMenuDTO> recursionMenu(Integer parentId, List<UserMenuDTO> list) {
        List<UserMenuDTO> result = new ArrayList<>();
        list.forEach(value -> {
            if (parentId.equals(value.getPid())) {
                value.setChildren(recursionMenu(value.getMid(), list));
                result.add(value);
                list.remove(value);
            }
        });
        return result;
    }

    @Override
    public HashMap<String, List<SysResource>> getRecursionTree() {
        List<SysResource> list = baseDao.findAllResourceList();
        List<SysResource> tree = recursionRes(-1, new CopyOnWriteArrayList<>(new ArrayList<>(list)));
        HashMap<String, List<SysResource>> map = new HashMap<>();
        map.put("list", list);
        map.put("tree", tree);
        return map;
    }

    /**
     * 将资源列表递归成树
     */
    private List<SysResource> recursionRes(Integer parentId, List<SysResource> list) {
        List<SysResource> result = new ArrayList<>();
        list.forEach(value -> {
            //!value.getResId().equals(value.getResPid()) 杜绝死循环情况
            if (parentId.equals(value.getResPid()) && !value.getResId().equals(value.getResPid())) {
                value.setChildren(recursionRes(value.getResId(), list));
                result.add(value);
                list.remove(value);
            }
        });
        return result;
    }

    @Override
    public boolean isExistResCode(String resCode) {
        //查询resCode是否存在
        SysResource sysResource = new SysResource();
        sysResource.setResCode(resCode);
        return baseDao.exsit(sysResource);
    }

    @Override
    public int save(SysResource sysResource) {
        SysResource newResource = new SysResource();
        baseDao.updateAdditionSeq(sysResource);
        newResource.setResPid(sysResource.getResPid());
        newResource.setResType(sysResource.getResType());
        newResource.setResName(sysResource.getResName());
        newResource.setResCode(sysResource.getResCode());
        newResource.setResIcon(sysResource.getResIcon());
        newResource.setCreateTime(new Date());
        newResource.setSeq(sysResource.getSeq());
        newResource.setCreateId(ShiroUtils.getUserId());
        newResource.setState(sysResource.getState());
        newResource.setResLevel(sysResource.getResLevel());
        return super.save(newResource);
    }

    @Override
    public int update(SysResource sysResource) {
        int resid = sysResource.getResId();
        int resPid = sysResource.getResPid();
        SysResource oldResource = findOne(resid);
        if (null == oldResource) {
            throw new InfoException("资源不存在");
        }
        if (resPid == resid) {
            throw new InfoException("不能选择自己作为上级资源");
        }
        SysResource newResource = new SysResource();
        newResource.setResId(resid);
        newResource.setResType(sysResource.getResType());
        newResource.setResCode(sysResource.getResCode());
        newResource.setResName(sysResource.getResName());
        newResource.setResPid(resPid);
        newResource.setResIcon(sysResource.getResIcon());
        newResource.setSeq(sysResource.getSeq());
        newResource.setState(sysResource.getState());
        newResource.setCreateTime(sysResource.getCreateTime());
        newResource.setResLevel(sysResource.getResLevel());
        if (resPid != oldResource.getResPid()) {
            //上级资源改变，原上级资源seq减修改
            baseDao.updateSubtractionSeq(oldResource);
            //新的上级资源seq加修改
            baseDao.updateAdditionSeq(newResource);
            //递归修改子资源级别
            updateChildLevel(newResource.getResId(), newResource.getResLevel());
        } else {
            if (!sysResource.getSeq().equals(oldResource.getSeq())) {
                //原上级资源seq减修改
                baseDao.updateSubtractionSeq(oldResource);
                //原上级资源seq加修改
                baseDao.updateAdditionSeq(newResource);
            }
        }
        return super.update(newResource);
    }

    @Override
    public int delete(Integer resId) {
        SysResource sysResource = baseDao.findOne(resId);
        if (sysResource == null) {
            throw new InfoException("资源ID [" + resId + "] 不存在");
        }

        //查询是否还有子节点
        SysResource sysRes = new SysResource();
        sysRes.setResPid(resId);
        List<SysResource> list = baseDao.findList(sysRes);
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

    /**
     * 递归修改子资源级别
     */
    private void updateChildLevel(int resId, int level) {
        //查询子资源
        SysResource resource = new SysResource();
        resource.setResPid(resId);
        List<SysResource> childRes = baseDao.findList(resource);
        for (SysResource s : childRes) {
            s.setResLevel(level + 1);
            baseDao.update(s);
            updateChildLevel(s.getResId(), s.getResLevel());
        }
    }

}
