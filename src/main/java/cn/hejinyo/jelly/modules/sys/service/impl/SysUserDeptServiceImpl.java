package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.utils.RecursionUtil;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.dao.SysUserDeptDao;
import cn.hejinyo.jelly.modules.sys.model.SysDeptEntity;
import cn.hejinyo.jelly.modules.sys.model.SysUserDeptEntity;
import cn.hejinyo.jelly.modules.sys.service.SysDeptService;
import cn.hejinyo.jelly.modules.sys.service.SysUserDeptService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 人员部门业务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:31
 */
@Service
public class SysUserDeptServiceImpl extends BaseServiceImpl<SysUserDeptDao, SysUserDeptEntity, Integer> implements SysUserDeptService {
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysDeptService sysDeptService;

    /**
     * 查询用户部门列表
     */
    @Override
    public List<SysUserDeptEntity> getUserDeptList(Integer userId) {
        return baseDao.findDeptListByUserId(userId);
    }

    /**
     * 删除用户部门关系
     */
    @Override
    public int deleteByUserId(Integer userId) {
        return baseDao.deleteByUserId(userId);
    }

    /**
     * 保存用户部门关系
     */
    @Override
    public int save(Integer userId, List<SysUserDeptEntity> userDeptList) {
        //先删除用户部门关系
        int count = baseDao.deleteByUserId(userId);
        if (userDeptList.size() == 0) {
            return count;
        }
        //保存部门与菜单关系
        for (SysUserDeptEntity dept : userDeptList) {
            SysUserDeptEntity userDeptEntity = new SysUserDeptEntity();
            userDeptEntity.setUserId(userId);
            userDeptEntity.setDeptId(dept.getDeptId());
            userDeptEntity.setCreateId(ShiroUtils.getUserId());
            count += baseDao.save(userDeptEntity);
        }
        return count;
    }

    /**
     * 查询用户部门Id列表,带缓存
     */
    @Override
    public List<Integer> getUserDeptId(Integer userId) {
        List<Integer> deptList = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_DEPT, new TypeToken<List<Integer>>() {
        }.getType());
        if (deptList == null) {
            deptList = baseDao.findDeptIdByUserId(userId);
            redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_DEPT, deptList);
        }
        return deptList;
    }

    /**
     * 获取用户所有的部门，包含子部门,递归查询处理
     */
    @Override
    public List<Integer> getUserAllDeptId(Integer userId) {
        List<Integer> allDeptList = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_DEPT, new TypeToken<List<Integer>>() {
        }.getType());
        if (allDeptList == null || allDeptList.size() < 1) {
            List<Integer> parentIdList = getUserDeptId(userId);
            List<SysDeptEntity> list = sysDeptService.getAllDeptList();
            allDeptList = recursionDept(true, new CopyOnWriteArrayList<>(list), parentIdList);
            redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_DEPT, allDeptList);
        }
        return allDeptList;
    }

    /**
     * 递归获取用户部门和子部门
     *
     * @param isRoot       是否显示根节点
     * @param list         需要遍历的列表
     * @param parentIdList 父节点编号列表
     */
    @Override
    public List<Integer> recursionDept(boolean isRoot, List<SysDeptEntity> list, List<Integer> parentIdList) {
        List<Integer> allDeptList = new ArrayList<>();
        RecursionUtil.list(allDeptList, SysDeptEntity.class, "getDeptId", true, new CopyOnWriteArrayList<>(list), parentIdList);
        return allDeptList;
    }

    /**
     * 根据用户ID，获取部门列表
     */
    @Override
    public List<SysDeptEntity> getDeptList(Integer userId) {
        return baseDao.findDeptList(userId);
    }

    /**
     * 查询用户部门列表,包含部门信息
     */
    @Override
    public List<SysUserDeptEntity> getUserDeptInfoList(Integer userId) {
        return baseDao.findUserDeptInfoList(userId);
    }

    /**
     * 根绝角色编号查询用户编号列表
     */
    @Override
    public List<Integer> getUserIdByDeptId(Integer deptId) {
        return baseDao.findUserIdByDeptId(deptId);
    }

    /**
     * 删除人员与部门关系
     */
    @Override
    public int deleteByDeptIds(Integer[] deptIds) {
        return baseDao.deleteByDeptIds(deptIds);
    }
}
