package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysConfigOptionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sys_config_option 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/15 22:58
 */
@Mapper
public interface SysConfigOptionDao extends BaseDao<SysConfigOptionEntity, Integer> {

    /**
     * 修改配置属性编码
     */
    void updateCode(@Param("oldCode") String oldCode, @Param("newCode") String newCode);

    /**
     * 根据配置code获取配置值列表
     */
    List<SysConfigOptionEntity> findOptionListByCode(String code);

    /**
     * 通过Code删除配置属性
     */
    int deleteByCode(String code);
}