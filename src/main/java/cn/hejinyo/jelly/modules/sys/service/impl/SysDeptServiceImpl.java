package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.PojoConvertUtil;
import cn.hejinyo.jelly.common.utils.RecursionUtil;
import cn.hejinyo.jelly.modules.sys.dao.SysDeptDao;
import cn.hejinyo.jelly.modules.sys.model.SysDeptEntity;
import cn.hejinyo.jelly.modules.sys.service.SysDeptService;
import cn.hejinyo.jelly.modules.sys.service.SysUserDeptService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils.getUserId;

/**
 * 部门业务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:31
 */
@Service
public class SysDeptServiceImpl extends BaseServiceImpl<SysDeptDao, SysDeptEntity, Integer> implements SysDeptService {

    @Autowired
    private SysUserDeptService sysUserDeptService;

    /**
     * 获取所有部门列表
     */
    @Override
    public List<SysDeptEntity> getAllDeptList() {
        return baseDao.findAllDeptList();
    }

    /**
     * 获取所有部门Id列表
     */
    @Override
    public List<Integer> getAllDeptIdList() {
        return getAllDeptList().stream().map(SysDeptEntity::getDeptId).collect(Collectors.toList());
    }

    /**
     * 部门管理树数据
     */
    @Override
    public HashMap<String, List<SysDeptEntity>> getRecursionTree(boolean showRoot) {
        List<Integer> parentIdList = new ArrayList<>();
        //系统管理员，拥有最高权限
        if (Constant.SUPER_ADMIN.equals(ShiroUtils.getUserId())) {
            parentIdList.add(Constant.TREE_ROOT);
        } else {
            parentIdList = sysUserDeptService.getUserDeptId(getUserId());
        }
        return RecursionUtil.listTree(showRoot, SysDeptEntity.class, "getDeptId", getAllDeptList(), parentIdList);
    }


    /**
     * 分页查询
     */
    @Override
    public List<SysDeptEntity> findPage(PageQuery pageQuery) {
        List<Integer> allDeptIdList = sysUserDeptService.getUserAllDeptId(getUserId());
        pageQuery.put("allDeptId", allDeptIdList);
        return super.findPage(pageQuery);
    }

    /**
     * 部门选择数据,排除状态为不为正常的组织
     */
    @Override
    public HashMap<String, List<SysDeptEntity>> getSelectTree(boolean showRoot) {
        List<Integer> parentIdList = new ArrayList<>();
        //系统管理员，拥有最高权限
        if (Constant.SUPER_ADMIN.equals(ShiroUtils.getUserId())) {
            parentIdList.add(Constant.TREE_ROOT);
        } else {
            parentIdList = sysUserDeptService.getUserDeptId(getUserId());
        }
        return RecursionUtil.listTree(showRoot, SysDeptEntity.class, "getDeptId", baseDao.findValidDeptList(), parentIdList);
    }

    /**
     * 新增部门
     */
    @Override
    public int save(SysDeptEntity dept) {
        // 检测越权
        if (checkPermission(dept.getParentId())) {
            SysDeptEntity newDept = PojoConvertUtil.convert(dept, SysDeptEntity.class);
            newDept.setCreateId(getUserId());

            //检查排序号，如果有相同的，将这个排序号后面的排序号递增，然后保存部门 TODO

            int count = super.save(newDept);
            if (count > 0) {

            }
            return count;
        }
        throw new InfoException(StatusCode.PERMISSION_UNAUTHORIZED);
    }

    /**
     * 修改部门
     */
    @Override
    public int update(SysDeptEntity dept) {
        if (Constant.TREE_ROOT.equals(dept.getDeptId())) {
            throw new InfoException(StatusCode.DATABASE_UPDATE_ROOT);
        }
        // 检测越权
        if (checkPermission(dept.getParentId())) {
            SysDeptEntity oldDept = baseDao.findOne(dept.getDeptId());
            if (!oldDept.getParentId().equals(dept.getParentId())) {
                //检查部门是否存在子节点
                Integer[] deptIds = {dept.getDeptId()};
                List<SysDeptEntity> children = baseDao.findDeptByParentIds(deptIds);
                if (children.size() > 0) {
                    throw new InfoException(StatusCode.DATABASE_UPDATE_CHILD);
                }
            }

            SysDeptEntity newDept = PojoConvertUtil.convert(dept, SysDeptEntity.class);
            newDept.setUpdateId(getUserId());
            //修改排序号，需要修改同级排序号 TODO
            //修改父节点，需要修改很多相关资源 TODO
            int count = super.update(newDept);
            if (count > 0) {

            }
            return count;
        }
        throw new InfoException(StatusCode.PERMISSION_UNAUTHORIZED);
    }

    /**
     * 删除部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(Integer[] deptIds) {
        for (Integer deptId : deptIds) {
            if (deptId.equals(Constant.TREE_ROOT)) {
                throw new InfoException(StatusCode.DATABASE_DELETE_ROOT);
            }
            if (!checkPermission(deptId)) {
                throw new InfoException(StatusCode.PERMISSION_UNAUTHORIZED);
            }
            //检查是否有人员在此部门下
            List<Integer> list = sysUserDeptService.getUserIdByDeptId(deptId);
            if (list.size() > 0) {
                throw new InfoException("部门 [ " + baseDao.findOne(deptId).getDeptName() + " ] 存在用户，不能被删除");
            }
        }

        //检查部门是否存在子节点
        List<SysDeptEntity> children = baseDao.findDeptByParentIds(deptIds);
        if (children.size() > 0) {
            throw new InfoException(StatusCode.DATABASE_DELETE_CHILD);
        }

        //删除人员与部门关系
        sysUserDeptService.deleteByDeptIds(deptIds);

        //删除序号，检查后面是否还有，有的话，需要递减 TODO

        //清除部门资源（调用客户相关方法，将商机转到其他部门什么的） TODO

        //记录删除日志

        int count = baseDao.deleteBatch(deptIds);
        if (count > 0) {

        }
        return count;
    }

    /**
     * 检测是否越权，增加和修改的部门是否在当前用户的部门之下
     */
    @Override
    public boolean checkPermission(Integer deptId) {
        // 管理员直接通过
        if (Constant.SUPER_ADMIN.equals(ShiroUtils.getUserId())) {
            return true;
        }
        List<Integer> allDeptIdList = sysUserDeptService.getUserAllDeptId(getUserId());
        for (Integer id : allDeptIdList) {
            if (deptId.equals(id)) {
                return true;
            }
        }
        return false;
    }
}
