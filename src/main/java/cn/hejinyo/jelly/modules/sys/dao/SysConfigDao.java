package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 22:15
 */
@Mapper
public interface SysConfigDao extends BaseDao<SysConfig, Long> {

    /**
     * 根据key，查询value
     */
    SysConfig findByKey(String paramKey);

    /**
     * 根据key，更新value
     *
     * @param key   key
     * @param value value
     * @return int
     */
    int updateValueByKey(@Param("key") String key, @Param("value") String value);

}
