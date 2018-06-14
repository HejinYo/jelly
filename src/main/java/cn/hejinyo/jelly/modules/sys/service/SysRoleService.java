package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysRoleEntity;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 17:04
 */
public interface SysRoleService extends BaseService<SysRoleEntity, Integer> {

    /**
     * 角色列表下拉选择select
     */
    List<SysRoleEntity> getDropList();
}
