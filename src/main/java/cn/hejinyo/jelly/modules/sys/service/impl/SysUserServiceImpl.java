package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.common.utils.StringUtils;
import cn.hejinyo.jelly.modules.sys.dao.SysUserDao;
import cn.hejinyo.jelly.modules.sys.model.SysRole;
import cn.hejinyo.jelly.modules.sys.model.SysUser;
import cn.hejinyo.jelly.modules.sys.model.SysUserRole;
import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
import cn.hejinyo.jelly.modules.sys.service.ShiroService;
import cn.hejinyo.jelly.modules.sys.service.SysRoleService;
import cn.hejinyo.jelly.modules.sys.service.SysUserRoleService;
import cn.hejinyo.jelly.modules.sys.service.SysUserService;
import cn.hejinyo.jelly.modules.sys.utils.ShiroUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
@Service
public class SysUserServiceImpl extends BaseServiceImpl<SysUserDao, SysUser, Integer> implements SysUserService {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ShiroService shiroService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(SysUser sysUser) {
        //创建新的 PO
        SysUser newUser = new SysUser();
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
            SysRole sysRole = sysRoleService.findOne(sysUser.getRoleId());
            if (sysRole != null) {
                sysUserRoleService.save(new SysUserRole(newUser.getUserId(), sysRole.getRoleId()));
            } else {
                throw new InfoException("所选角色不存在，请刷新后重试");
            }
        }
        return result;
    }

    /**
     * 检查用户名是否存在，加锁
     */
    @Override
    public boolean isExistUserName(String userName) {
        //查询用户名是否存在
        SysUser sysUser = new SysUser();
        sysUser.setUserName(StringUtils.toLowerCase(userName));
        return baseDao.exsit(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(SysUser sysUser) {
        //用户原来信息
        SysUser sysUserOld = baseDao.findOne(sysUser.getUserId());
        if (null == sysUserOld) {
            throw new InfoException("用户不存在");
        }
        //修改标志
        boolean flag = Boolean.FALSE;
        //新的PO
        SysUser newUser = new SysUser();
        newUser.setUserId(sysUser.getUserId());

        String userStr = sysUser.getUserName();
        String pwdStr = sysUser.getUserPwd();
        String email = sysUser.getEmail();
        String phone = sysUser.getPhone();
        Date loginTime = sysUser.getLoginTime();
        Integer state = sysUser.getState();

        //校验用户名是否修改
        String userName = StringUtils.toLowerCase(userStr);
        if (!userName.equals(sysUserOld.getUserName())) {
            if (isExistUserName(userName)) {
                //新的用户名已经存在
                throw new InfoException("用户名已经存在");
            }
            newUser.setUserName(userName);
            flag = true;
        }
        //密码是否修改
        if (StringUtils.isNotNull(pwdStr)) {
            //加密新密码
            String userPwd = ShiroUtils.userPassword(sysUser.getUserPwd(), sysUserOld.getUserSalt());
            if (!userPwd.equals(sysUserOld.getUserPwd())) {
                //用户盐,随机数
                String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
                newUser.setUserSalt(salt);
                //加密密码
                newUser.setUserPwd(ShiroUtils.userPassword(sysUser.getUserPwd(), salt));
                flag = true;
            }
        }
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
        //登录时间是否修改
        if (null != loginTime) {
            if (!loginTime.equals(sysUserOld.getLoginTime())) {
                newUser.setLoginTime(loginTime);
                flag = true;
            }
        }
        //状态是否修改
        if (!sysUserOld.getState().equals(state)) {
            newUser.setState(state);
            flag = true;
        }
        int result = 0;
        //角色是否修改
        if (!sysUser.getRoleId().equals(sysUserOld.getRoleId())) {
            newUser.setRoleId(sysUser.getRoleId());
            result = baseDao.updateUserRole(newUser);
            if (result == 0) {
                result = baseDao.saveUserRole(newUser);
            }
            //清除redis中的权限缓存
            redisUtils.cleanKey(RedisKeys.getAuthCacheKey(sysUser.getUserName() + "*"));
        }

        if (flag) {
            result = baseDao.update(newUser);
        }
        return result;
    }

    @Override
    public int updateUserLoginInfo(CurrentUserDTO userDTO) {
        SysUser sysUser = new SysUser();
        sysUser.setLoginTime(new Date());
        sysUser.setUserId(userDTO.getUserId());
        sysUser.setLoginIp(userDTO.getLoginIp());
        return baseDao.update(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(Integer[] ids) {
        //删除用户角色表记录
        sysUserRoleService.deleteListByUserId(ids);

        return baseDao.deleteBatch(ids);
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

        SysUser sysUserOld = baseDao.findOne(ShiroUtils.getUserId());
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
        SysUser newUser = new SysUser();
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
    public int updateUserInfo(SysUser sysUser) {
        //用户原来信息
        SysUser sysUserOld = baseDao.findOne(ShiroUtils.getUserId());
        //修改标志
        boolean flag = Boolean.FALSE;
        //新的PO
        SysUser newUser = new SysUser();
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
    public int updateUserAvatar(SysUser sysUser) {
        return baseDao.update(sysUser);
    }


    /**
     * 更新用户redis信息
     */
    @Override
    public void updateUserRedisInfo() {
        CurrentUserDTO oldUser = ShiroUtils.getCurrentUser();
        CurrentUserDTO userDTO = shiroService.getCurrentUser(oldUser.getUserName());
        userDTO.setUserToken(oldUser.getUserToken());
        userDTO.setLoginIp(oldUser.getLoginIp());
        userDTO.setLoginTime(oldUser.getLoginTime());
        //token写入缓存
        redisUtils.set(RedisKeys.getTokenCacheKey(userDTO.getUserName()), userDTO, 1800);
    }

}
