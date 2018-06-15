package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysConfigEntity;
import cn.hejinyo.jelly.modules.sys.model.SysConfigOptionEntity;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 22:16
 */
public interface SysConfigService extends BaseService<SysConfigEntity, Integer> {

    /**
     * 根据配置code获取配置值列表
     */
    List<SysConfigOptionEntity> getOptionListByCode(String code);

    /**
     * 保存配置属性
     */
    int saveOption(SysConfigOptionEntity config);

    /**
     * 修改配置属性
     */
    int updateOption(Integer optionId, SysConfigOptionEntity config);

    /**
     * 删除配置属性
     */
    int deleteBatchOption(Integer optionId);

    /**
     * 根据code获取系统配置信息
     */
    String getConfig(String code);

    /**
     * 根据code获取系统配置信息
     */
    <T> T getConfig(String code, Class<T> cass);

}
