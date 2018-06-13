package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * sys_log 持久化层
 * 
 * @author : HejinYo   hejinyo@gmail.com 
 * @date : 2018/06/13 23:29
 */
@Mapper
public interface SysLogDao extends BaseDao<SysLogEntity, Integer> {
}