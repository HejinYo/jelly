package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 16:54
 */
@Mapper
public interface SysRoleDao extends BaseDao<SysRole, Integer> {
    /**
     * 角色列表下拉选择select
     */
    List<SysRole> roleSelect();
}