package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.annotation.DataFilter;
import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.sys.dao.SysDeptDao;
import cn.hejinyo.jelly.modules.sys.model.SysDeptEntity;
import cn.hejinyo.jelly.modules.sys.service.SysDeptService;
import cn.hejinyo.jelly.modules.sys.service.SysUserDeptService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 部门业务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:31
 */
@Service
public class SysDeptServiceImpl extends BaseServiceImpl<SysDeptDao, SysDeptEntity, Integer> implements SysDeptService {

    /**
     * 递归部门获取id的名称
     */
    private static final String GET_ID_NAME = "getDeptId";

    @Autowired
    private SysUserDeptService sysUserDeptService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 获取系统所有部门列表
     */
    @Override
    public List<SysDeptEntity> getAllDeptList() {
        return baseDao.findAllDeptList();
    }

    /**
     * 获取系统所有有效部门列表，状态正常
     */
    @Override
    public List<SysDeptEntity> getValidDeptList() {
        return baseDao.findValidDeptList();
    }

    /**
     * 指定一个节点，在系统所有部门中 递归遍历  指定节点开始 的所有子节点 为列表
     *
     * @param isRoot       是否显示根节点
     * @param parentIdList 父节点编号列表
     */
    @Override
    public List<Integer> recursionDept(boolean isRoot, List<Integer> parentIdList) {
        List<Integer> allDeptList = new ArrayList<>();
        // 遍历系统所有部门
        List<SysDeptEntity> list = getAllDeptList();
        RecursionUtil.list(allDeptList, SysDeptEntity.class, GET_ID_NAME, isRoot, new CopyOnWriteArrayList<>(list), parentIdList);
        return allDeptList;
    }

    /**
     * 部门树数据
     *
     * @param valid    部门有效状态
     * @param showRoot 是否显示根节点
     */
    @Override
    public HashMap<String, List<SysDeptEntity>> getDeptListTree(boolean valid, boolean showRoot) {
        List<Integer> parentIdList = new ArrayList<>();
        //系统管理员，拥有最高权限
        if (Constant.SUPER_ADMIN.equals(ShiroUtils.getUserId())) {
            parentIdList.add(Constant.TREE_ROOT);
        } else {
            // 查询用户所在部门的ID列表，作为根节点
            parentIdList = sysUserDeptService.getCurDeptIdListByUserId(ShiroUtils.getUserId());
        }
        List<SysDeptEntity> list = valid ? getValidDeptList() : getAllDeptList();
        return RecursionUtil.listTree(showRoot, SysDeptEntity.class, GET_ID_NAME, list, parentIdList);
    }

    /**
     * 分页查询
     */
    @Override
    @DataFilter(dept = Constant.Dept.SUB_DEPT)
    public List<SysDeptEntity> findPage(PageQuery pageQuery) {
        //系统管理员，拥有最高权限
        if (Constant.SUPER_ADMIN.equals(ShiroUtils.getUserId())) {
            return super.findPage(pageQuery);
        }
        return super.findPage(pageQuery);
    }

    /**
     * 新增部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(SysDeptEntity dept) {
        if (baseDao.findOne(dept.getParentId()) == null) {
            // 检查父节点是否存在
            throw new InfoException(StatusCode.DATABASE_NO_FATHER);
        }

        //检测越权,只能在自己部门范围内进行增加
        if (checkPermission(true, dept.getParentId())) {
            // 对象拷贝
            SysDeptEntity newDept = PojoConvertUtil.convert(dept, SysDeptEntity.class);
            newDept.setCreateId(ShiroUtils.getUserId());

            int count = super.save(newDept);
            if (count > 0) {
                // 清除所有用户的部门缓存
                cleanDeptCache();
                // 重排序
                changeSort(newDept, newDept.getSeq());
                //返回主键
                return newDept.getDeptId();
            }
            return count;
        }
        throw new InfoException(StatusCode.PERMISSION_UNAUTHORIZED);
    }

    /**
     * 修改部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Integer deptId, SysDeptEntity dept) {
        if (Constant.TREE_ROOT.equals(deptId)) {
            // 根节点不能修改
            throw new InfoException(StatusCode.DATABASE_UPDATE_ROOT);
        }
        Integer parentId = dept.getParentId();
        SysDeptEntity oldDept = baseDao.findOne(deptId);
        // 检测越权，只能编辑用户所拥有的子部门，所在部门不能编辑
        if (checkPermission(true, parentId)) {
            //如果部门修改了父节点，需要检测新的父节点是否是当前节点的子节点，如果是，会造成递归死循环
            if (parentId != null && !oldDept.getParentId().equals(parentId)) {
                if (baseDao.findOne(parentId) == null) {
                    // 检查父节点是否存在
                    throw new InfoException(StatusCode.DATABASE_NO_FATHER);
                }
                // 递归获取当前节点的所有子节点
                List<Integer> allIdList = recursionDept(true, Collections.singletonList(deptId));
                //检查新的父节点是否在子节点列表内
                if (allIdList.contains(parentId)) {
                    throw new InfoException(StatusCode.DATABASE_UPDATE_LOOP);
                }
            }
            SysDeptEntity newDept = PojoConvertUtil.convert(dept, SysDeptEntity.class);
            newDept.setUpdateId(ShiroUtils.getUserId());
            newDept.setDeptId(deptId);
            int count = super.update(newDept);
            if (count > 0) {
                // 清除所有用户的部门缓存
                cleanDeptCache();
            }
            //如果修改了排序号
            Integer seq = newDept.getSeq();
            if (seq != null && !seq.equals(oldDept.getSeq())) {
                changeSort(newDept, seq);
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
    public int delete(Integer deptId) {
        if (deptId.equals(Constant.TREE_ROOT)) {
            //根节点不允许删除
            throw new InfoException(StatusCode.DATABASE_DELETE_ROOT);
        }
        if (!checkPermission(false, deptId)) {
            // 检查越权，只能删除用户子部门，用户所属部门不能删除
            throw new InfoException(StatusCode.PERMISSION_UNAUTHORIZED);
        }
        //检查是否有人员在此部门下
        List<Integer> list = sysUserDeptService.getUserIdByDeptId(deptId);
        if (list.size() > 0) {
            throw new InfoException("部门 [ " + baseDao.findOne(deptId).getDeptName() + " ] 存在用户，不能被删除");
        }

        //检查部门是否存在子节点
        List<SysDeptEntity> children = baseDao.findDeptByParentId(deptId);
        if (children.size() > 0) {
            throw new InfoException(StatusCode.DATABASE_DELETE_CHILD);
        }

        //删除人员与部门关系
        sysUserDeptService.deleteByDeptId(deptId);

        SysDeptEntity sysDeptEntity = baseDao.findOne(deptId);
        int count = baseDao.delete(deptId);
        if (count > 0) {
            // 清除所有用户的部门缓存
            cleanDeptCache();
            // 原来同级节点重排序
            reorder(sysDeptEntity.getParentId());
        }
        return count;
    }

    /**
     * 检测是否越权，增加和修改的部门是否在当前用户的部门之下
     *
     * @param showSelf 是否包含用户所在部门
     * @param deptId   需要检测的部门ID
     */
    @Override
    public boolean checkPermission(Boolean showSelf, Integer deptId) {
        // 管理员直接通过
        if (Constant.SUPER_ADMIN.equals(ShiroUtils.getUserId())) {
            return true;
        }
        List<Integer> deptIdList;

        if (showSelf) {
            deptIdList = sysUserDeptService.getAllDeptIdListByUserId(ShiroUtils.getUserId());
        } else {
            deptIdList = sysUserDeptService.getSubDeptIdListByUserId(ShiroUtils.getUserId());
        }

        return deptIdList.contains(deptId);
    }

    /**
     * 清除所有用户的部门缓存
     */
    private void cleanDeptCache() {
        Set<String> userStore = redisUtils.keys(RedisKeys.storeUser("*"));
        userStore.forEach(s -> {
            redisUtils.hdel(s, RedisKeys.USER_CUR_DEPT);
            redisUtils.hdel(s, RedisKeys.USER_SUB_DEPT);
            redisUtils.hdel(s, RedisKeys.USER_ALL_DEPT);
        });
    }

    /**
     * 节点拖动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int nodeDrop(String location, Integer deptId, Integer inDeptId) {
        // 拖动节点
        SysDeptEntity currRes = baseDao.findOne(deptId);
        // 进入节点
        SysDeptEntity innerRes = baseDao.findOne(inDeptId);

        // 任何一个节点不存在，不进行操作
        if (currRes == null || innerRes == null) {
            return 0;
        }

        Integer currParentId = currRes.getParentId();
        Integer innerParentId = innerRes.getParentId();
        // 查询进入节点所有同级节点
        List<SysDeptEntity> innerList = baseDao.findListByParentId(innerParentId);
        //判断是不是同一个父节点
        boolean parentEquals = currParentId.equals(innerParentId);
        if (parentEquals) {
            // 去掉进入节点在同级排序
            innerList.removeIf(dept -> dept.getDeptId().equals(deptId));
        }

        int innerSize = innerList.size();
        int innerSeq = 1;
        int add = 1;
        int count;
        switch (location) {
            // 进入节点及其后面的依次加1，被拖拽节点排序号设置为进入节点
            case "before":
                for (int i = 0; i < innerSize; i++) {
                    if (innerList.get(i).getDeptId().equals(inDeptId)) {
                        innerSeq += i;
                        // 拖动节点新位置后面的排序加1
                        add = 2;
                    }
                    innerList.get(i).setSeq(i + add);
                }
                // 修改进入节点所有排序
                baseDao.updateInnerAllSeq(innerList);
                // 修改拖动节点
                count = baseDao.updateParentIdAndSeq(deptId, innerParentId, innerSeq);
                break;
            case "after":
                for (int i = 0; i < innerSize; i++) {
                    innerList.get(i).setSeq(i + add);
                    if (innerList.get(i).getDeptId().equals(inDeptId)) {
                        innerSeq += i;
                        add = 2;
                    }
                }
                // 修改进入节点所有排序
                baseDao.updateInnerAllSeq(innerList);
                // 修改拖动节点
                count = baseDao.updateParentIdAndSeq(deptId, innerParentId, innerSeq + 1);
                break;
            case "inner":
                // 被拖拽节点父节点被进入节点，序号为进入节点的子节点长度+1
                count = baseDao.updateParentIdAndSeq(deptId, inDeptId, baseDao.findListByParentId(inDeptId).size() + 1);
                break;
            default:
                return 0;
        }

        // 不同父节点，更新拖动节点原来排序
        if (!parentEquals) {
            reorder(currParentId, deptId);
        }
        return count;
    }


    /**
     * 同级修改排序
     */
    private int changeSort(SysDeptEntity sysDeptEntity, Integer seq) {
        // 查询拖动节点所有同级节点
        List<SysDeptEntity> innerList = baseDao.findListByParentId(sysDeptEntity.getParentId());
        // 最大排序为同级长度
        innerList.removeIf(res -> res.getDeptId().equals(sysDeptEntity.getDeptId()));
        int innerSize = innerList.size();
        int innerSeq = 1;
        if(innerSize>0){
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
        return baseDao.updateParentIdAndSeq(sysDeptEntity.getDeptId(), sysDeptEntity.getParentId(), innerSeq);
    }

    /**
     * 父节点资源重排序
     */
    private void reorder(Integer parentId, Integer... resIds) {
        // 查询拖动节点所有同级节点
        List<SysDeptEntity> currList = baseDao.findListByParentId(parentId);
        int currSize = currList.size();
        if (currSize > 0) {
            for (Integer resId : resIds) {
                // 去掉拖动节点在同级排序
                currList.removeIf(res -> res.getDeptId().equals(resId));
            }
            for (int i = 0; i < currSize; i++) {
                currList.get(i).setSeq(i + 1);
            }
            // 修改拖动节点原来位置所有排序
            baseDao.updateInnerAllSeq(currList);
        }

    }
}
