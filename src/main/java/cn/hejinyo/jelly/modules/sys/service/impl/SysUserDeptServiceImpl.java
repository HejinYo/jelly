package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.dao.SysUserDeptDao;
import cn.hejinyo.jelly.modules.sys.model.SysUserDeptEntity;
import cn.hejinyo.jelly.modules.sys.service.SysDeptService;
import cn.hejinyo.jelly.modules.sys.service.SysUserDeptService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 人员部门业务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:31
 */
@Service
public class SysUserDeptServiceImpl extends BaseServiceImpl<SysUserDeptDao, SysUserDeptEntity, Integer> implements SysUserDeptService {

    private final Type LIST_INTEGER_TYPE = new TypeToken<List<Integer>>() {
    }.getType();

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysDeptService sysDeptService;

    /**
     * 用户所在部门Id列表，带缓存
     */
    @Override
    public List<Integer> getCurDeptIdListByUserId(Integer userId) {
        // 查询用户缓存中的部门ID列表
        Optional<List<Integer>> deptList = Optional.ofNullable(redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_CUR_DEPT, LIST_INTEGER_TYPE));

        return deptList.orElseGet(() -> {
            // 缓存中没有用户部门信息，则从数据库中查询，并写入用户缓存
            List<Integer> newList = baseDao.findDeptIdListByUserId(userId);
            redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_CUR_DEPT, newList);
            return newList;
        });
    }

    /**
     * 用户所在部门和子部门，带缓存
     */
    @Override
    public List<Integer> getAllDeptIdListByUserId(Integer userId) {
        // 查询用户缓存中的部门ID列表
        Optional<List<Integer>> userAllDeptList = Optional.ofNullable(redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_ALL_DEPT, LIST_INTEGER_TYPE));

        return userAllDeptList.orElseGet(() -> {
            // 用户所在部门作为根节点进行递归
            List<Integer> parentIdList = this.getCurDeptIdListByUserId(userId);
            // 显示根节点，也就是显示用户所在部门
            List<Integer> newList = sysDeptService.recursionDept(true, parentIdList);
            redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_ALL_DEPT, newList);
            return newList;
        });
    }

    /**
     * 用户拥有的子部门，带缓存
     */
    @Override
    public List<Integer> getSubDeptIdListByUserId(Integer userId) {
        // 查询用户缓存中的部门ID列表
        Optional<List<Integer>> userSubDeptList = Optional.ofNullable(redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_SUB_DEPT, LIST_INTEGER_TYPE));

        return userSubDeptList.orElseGet(() -> {
            // 用户所在部门作为根节点进行递归
            List<Integer> parentIdList = this.getCurDeptIdListByUserId(userId);
            // 不显示根节点，只查询用户的所有子部门
            List<Integer> newList = sysDeptService.recursionDept(false, parentIdList);
            redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_SUB_DEPT, newList);
            return newList;
        });
    }


    /**
     * 根据用户编号，删除与此用户的部门关系
     */
    @Override
    public int deleteByUserId(Integer userId) {
        return baseDao.deleteByUserId(userId);
    }

    /**
     * 根据部门编号，删除与此部门的用户关系
     */
    @Override
    public int deleteByDeptId(Integer deptId) {
        return baseDao.deleteByDeptId(deptId);
    }

    /**
     * 保存用户部门关系
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(Integer userId, List<Integer> deptList) {
        //先删除用户部门关系
        int count = baseDao.deleteByUserId(userId);
        if (deptList.size() == 0) {
            return count;
        }
        //保存部门与用户关系
        List<SysUserDeptEntity> userDeptList = new ArrayList<>();
        for (Integer permId : deptList) {
            SysUserDeptEntity userDept = new SysUserDeptEntity();
            userDept.setUserId(userId);
            userDept.setDeptId(permId);
            userDept.setCreateId(ShiroUtils.getUserId());
            userDeptList.add(userDept);
        }

        return baseDao.saveBatch(userDeptList);
    }

    /**
     * 根据部门编号，查询部门下的用户ID列表
     */
    @Override
    public List<Integer> getUserIdByDeptId(Integer deptId) {
        return baseDao.findUserIdByDeptId(deptId);
    }

}
