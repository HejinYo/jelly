package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.modules.sys.model.SysDictEntity;
import cn.hejinyo.jelly.modules.sys.model.SysDictOptionEntity;
import cn.hejinyo.jelly.modules.sys.model.dto.DictDTO;

import java.util.List;

/**
 * 数据字典配置
 *
 * @author : heshuangshuang
 * @date : 2018/4/10 17:30
 */
public interface SysDictService extends BaseService<SysDictEntity, Integer> {

    /**
     * 查询配置字典列表
     */
    List<SysDictEntity> getDictList();

    /**
     * 获取数据字典项
     */
    List<DictDTO> getDictOptionByCode(String code);

    /**
     * 保存字典配置
     */
    int saveOption(SysDictOptionEntity option);

    /**
     * 修改字典配置
     */
    int updateOption(SysDictOptionEntity option);

    /**
     * 字典属性分页查询
     */
    List<SysDictOptionEntity> optionFindPage(PageQuery pageQuery);
}
