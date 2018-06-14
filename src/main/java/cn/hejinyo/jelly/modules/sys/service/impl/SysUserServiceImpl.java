package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.annotation.DataFilter;
import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.sys.dao.SysUserDao;
import cn.hejinyo.jelly.modules.sys.model.SysUserEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.service.*;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
@Service
public class SysUserServiceImpl extends BaseServiceImpl<SysUserDao, SysUserEntity, Integer> implements SysUserService {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ShiroService shiroService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysUserDeptService sysUserDeptService;

    /**
     * 查询一个用户信息
     */
    @Override
    public SysUserEntity findOne(Integer userId) {
        SysUserEntity user = baseDao.findOne(userId);
        if (user != null) {
            //获取用户所属的角色列表
            List<Integer> roleIdList = sysUserRoleService.getRoleIdListByUserId(userId);
            user.setRoleIdList(roleIdList);
            //获取用户所在部门
            List<Integer> deptIdList = sysUserDeptService.getCurDeptIdListByUserId(userId);
            user.setDeptIdList(deptIdList);
        }
        return user;
    }

    /**
     * 分页查询用户信息
     */
    @Override
    @DataFilter(dept = Constant.Dept.ALL_DEPT)
    public List<SysUserEntity> findPage(PageQuery pageQuery) {
        // 管理树点击的查询条件
        String treeValue = MapUtils.getString(pageQuery, "treeValue");
        if (StringUtils.isNotBlank(treeValue)) {
            // 获取部门下所有子部门,作为查询条件
            List<Integer> allDeptList = sysDeptService.recursionDept(true, Collections.singletonList(Integer.valueOf(treeValue)));
            pageQuery.put(MapUtils.getString(pageQuery, "treeKey"), allDeptList);
        }

        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize(), pageQuery.getOrder());
        List<SysUserEntity> list = baseDao.findPage(pageQuery);
        list.forEach(user -> {
            //查询用户角色
            user.setRoleIdList(sysUserRoleService.getRoleIdListByUserId(user.getUserId()));
            //查询用户部门
            user.setDeptIdList(sysUserDeptService.getCurDeptIdListByUserId(user.getUserId()));
        });
        return list;
    }

    /**
     * 更新用户登录信息
     */
    @Override
    public void updateUserLoginInfo(Integer userId) {
        SysUserEntity sysUser = new SysUserEntity();
        sysUser.setUserId(userId);
        //设置本地登录IP
        sysUser.setLoginIp(WebUtils.getIpAddr(WebUtils.getHttpServletRequest()));
        sysUser.setLoginTime(new Date());
        baseDao.update(sysUser);
    }

    /**
     * 保存用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(SysUserEntity sysUser) {
        //创建新的 PO
        SysUserEntity newUser = new SysUserEntity();
        //用户名小写
        newUser.setUserName(StringUtils.toLowerCase(sysUser.getUserName()));
        //用户盐,随机数
        String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        newUser.setUserSalt(salt);
        //加密密码
        newUser.setUserPwd(ShiroUtils.userPassword(sysUser.getUserPwd(), salt));
        //邮箱
        newUser.setEmail(sysUser.getEmail());
        //手机
        newUser.setPhone(sysUser.getPhone());
        //创建人员
        newUser.setCreateId(ShiroUtils.getUserId());
        //创建时间
        newUser.setCreateTime(new Date());
        //默认状态：正常
        newUser.setState(sysUser.getState());
        int result = baseDao.save(newUser);
        if (result > 0) {
            //保存角色关系
            sysUserRoleService.save(newUser.getUserId(), sysUser.getRoleIdList());
            //保存部门关系
            sysUserDeptService.save(newUser.getUserId(), sysUser.getDeptIdList());
        }
        return result;
    }

    /**
     * 检查用户名是否存在，加锁
     */
    @Override
    public boolean isExistUserName(String userName) {
        //查询用户名是否存在
        SysUserEntity sysUser = new SysUserEntity();
        sysUser.setUserName(StringUtils.toLowerCase(userName));
        return baseDao.exsit(sysUser);
    }

    /**
     * 更新用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Integer userId, SysUserEntity sysUser) {
        //非超级管理员不能修改自己
        if (!Constant.SUPER_ADMIN.equals(ShiroUtils.getUserId()) && userId.equals(ShiroUtils.getUserId())) {
            throw new InfoException(StatusCode.PERMISSION_ONESELF);
        }
        //用户原来信息
        SysUserEntity sysUserOld = baseDao.findOne(userId);
        if (null == sysUserOld) {
            throw new InfoException(StatusCode.DATABASE_SELECT_FAILURE);
        }

        // 查询用户原来的部门列表
        List<Integer> oldUserDeptList = sysUserDeptService.getCurDeptIdListByUserId(userId);
        List<Integer> newUserDeptList = sysUser.getDeptIdList();

        //如果修改了部门
        if (isChangeDept(oldUserDeptList, newUserDeptList)) {
            //旧部门检测越权，不能修改拥有其他非操作用户的部门的用户
            for (Integer oldDeptId : oldUserDeptList) {
                if (!sysDeptService.checkPermission(false, oldDeptId)) {
                    throw new InfoException(StatusCode.PERMISSION_UNAUTHORIZED);
                }
            }
            // 新部门检测越权
            for (Integer userDeptId : newUserDeptList) {
                if (!sysDeptService.checkPermission(true, userDeptId)) {
                    throw new InfoException(StatusCode.PERMISSION_UNAUTHORIZED);
                }
            }
        }

        //新的PO
        SysUserEntity newUser = new SysUserEntity();
        newUser.setUserId(userId);
        newUser.setUserName(StringUtils.toLowerCase(sysUser.getUserName()));
        newUser.setEmail(sysUser.getEmail());
        newUser.setPhone(sysUser.getPhone());
        newUser.setState(sysUser.getState());
        newUser.setUpdateId(ShiroUtils.getUserId());
        boolean isChangePassword = false;
        //密码是否修改
        if (StringUtils.isNotNull(sysUser.getUserPwd())) {
            //加密新密码
            String userPwd = ShiroUtils.userPassword(sysUser.getUserPwd(), sysUserOld.getUserSalt());
            if (!userPwd.equals(sysUserOld.getUserPwd())) {
                //用户盐,随机数
                String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
                newUser.setUserSalt(salt);
                //加密密码
                newUser.setUserPwd(ShiroUtils.userPassword(sysUser.getUserPwd(), salt));
                isChangePassword = true;
            }
        }
        int result = baseDao.update(newUser);
        if (result > 0) {
            //保存用户与角色关系
            sysUserRoleService.save(userId, sysUser.getRoleIdList());
            //保存用户与部门关系
            sysUserDeptService.save(userId, sysUser.getDeptIdList());
            // 清空这个用户的授权缓存
            redisUtils.hdel(RedisKeys.storeUser(userId), RedisKeys.USER_ROLE);
            redisUtils.hdel(RedisKeys.storeUser(userId), RedisKeys.USER_PERM);
            redisUtils.hdel(RedisKeys.storeUser(userId), RedisKeys.USER_CUR_DEPT);
            redisUtils.hdel(RedisKeys.storeUser(userId), RedisKeys.USER_SUB_DEPT);
            redisUtils.hdel(RedisKeys.storeUser(userId), RedisKeys.USER_ALL_DEPT);
            //修改了密码
            if (isChangePassword) {
                //清空用户认证缓存 需要重新登录
                redisUtils.hdel(RedisKeys.storeUser(userId), RedisKeys.USER_TOKEN);
            }
        }
        return result;
    }

    /**
     * 检查是否修改了部门
     */
    private boolean isChangeDept(List<Integer> oldUserDeptList, List<Integer> userDeptList) {
        int oldSize = oldUserDeptList.size();
        int newSize = userDeptList.size();
        //部门数量不等，一定修改了部门
        if (oldSize != newSize) {
            return true;
        }
        int count = 0;
        for (Integer oldDeptId : oldUserDeptList) {
            for (Integer newDeptId : userDeptList) {
                if (newDeptId.equals(oldDeptId)) {
                    count++;
                }
            }
        }
        // 如果新旧部门匹配的数量和原来部门数量不一致，一定修改了部门
        return oldSize != count;
    }

    /**
     * 删除用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(Integer[] userIds) {
        int count = baseDao.deleteBatch(userIds);
        //清除用户资源（
        if (count > 0) {
            for (Integer userId : userIds) {
                //删除用户角色表记录
                sysUserRoleService.deleteByUserId(userId);
                //删除用户与部门关系
                sysUserDeptService.deleteByUserId(userId);
                // 清空这个用户所有缓存
                redisUtils.delete(RedisKeys.storeUser(userId));
            }
        }
        return count;
    }

    /**
     * 修改密码
     */
    @Override
    public int updatePassword(HashMap<String, Object> param) {
        String oldPassword = MapUtils.getString(param, "oldPass");
        String newPassword = MapUtils.getString(param, "newPass");
        if (StringUtils.isEmpty(oldPassword)) {
            throw new InfoException("旧密码不能为空");
        }
        if (StringUtils.isEmpty(newPassword)) {
            throw new InfoException("新密码不能为空");
        }

        SysUserEntity sysUserOld = baseDao.findOne(ShiroUtils.getUserId());
        //旧密码错误
        if (!sysUserOld.getUserPwd().equals(ShiroUtils.userPassword(oldPassword, sysUserOld.getUserSalt()))) {
            throw new InfoException("原密码错误");
        }
        //新密码与原密码
        if (oldPassword.equals(newPassword)) {
            throw new InfoException("密码未修改");
        }
        //用户盐,随机数
        String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        SysUserEntity newUser = new SysUserEntity();
        newUser.setUserId(sysUserOld.getUserId());
        newUser.setUserSalt(salt);
        //加密密码
        newUser.setUserPwd(ShiroUtils.userPassword(newPassword, salt));
        return baseDao.update(newUser);
    }

    /**
     * 修改个人信息
     */
    @Override
    public int updateUserInfo(SysUserEntity sysUser) {
        //用户原来信息
        SysUserEntity sysUserOld = baseDao.findOne(ShiroUtils.getUserId());
        //修改标志
        boolean flag = Boolean.FALSE;
        //新的PO
        SysUserEntity newUser = new SysUserEntity();
        newUser.setUserId(sysUser.getUserId());
        String email = sysUser.getEmail();
        String phone = sysUser.getPhone();
        //邮箱是否修改
        if (!email.equals(sysUserOld.getEmail())) {
            newUser.setEmail(email);
            flag = true;
        }
        //手机是否修改
        if (!phone.equals(sysUserOld.getPhone())) {
            newUser.setPhone(phone);
            flag = true;
        }
        if (flag) {
            return baseDao.update(newUser);
        }
        return 0;
    }


    /**
     * 修改头像
     */
    @Override
    public int updateUserAvatar(SysUserEntity sysUser) {
        return baseDao.update(sysUser);
    }

    /**
     * 更新用户redis信息
     */
    @Override
    public void updateUserRedisInfo() {
        LoginUserDTO oldUser = ShiroUtils.getLoginUser();
        LoginUserDTO userDTO = shiroService.getLoginUser(oldUser.getUserName());
        userDTO.setUserToken(oldUser.getUserToken());
        userDTO.setLoginIp(oldUser.getLoginIp());
        userDTO.setLoginTime(oldUser.getLoginTime());
        //token写入缓存
        redisUtils.hset(RedisKeys.storeUser(userDTO.getUserId()), RedisKeys.USER_TOKEN, userDTO);
    }

}
