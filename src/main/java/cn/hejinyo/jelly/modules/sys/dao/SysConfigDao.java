package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * sys_config 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/15 22:57
 */
@Mapper
public interface SysConfigDao extends BaseDao<SysConfigEntity, Integer> {
    /**
     * 根据code获取系统配置信息
     */
    SysConfigEntity getConfig(String code);
}