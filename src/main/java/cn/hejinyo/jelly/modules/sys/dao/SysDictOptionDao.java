package cn.hejinyo.jelly.modules.sys.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.sys.model.SysDictOptionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sys_dict_option 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/06/15 23:59
 */
@Mapper
public interface SysDictOptionDao extends BaseDao<SysDictOptionEntity, Integer> {
    /**
     * 修改配置属性编码
     */
    int updateCode(@Param("oldCode") String oldCode, @Param("newCode") String newCode);

    /**
     * 获取数据字典项
     */
    List<SysDictOptionEntity> getDictOptionByCode(String code);
}