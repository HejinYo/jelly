package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * sys_user 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/14 23:21
 */
@Mapper
public interface SysUserDao extends BaseDao<SysUserEntity, Integer> {

    int saveUserRole(SysUserEntity sysUser);

    int updateUserRole(SysUserEntity sysUser);
}