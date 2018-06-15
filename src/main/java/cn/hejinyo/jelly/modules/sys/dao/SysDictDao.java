package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysDictEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * sys_dict 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/15 23:59
 */
@Mapper
public interface SysDictDao extends BaseDao<SysDictEntity, Integer> {
    /**
     * 根据编码查询数据字典目录
     */
    SysDictEntity findByCode(String code);

}