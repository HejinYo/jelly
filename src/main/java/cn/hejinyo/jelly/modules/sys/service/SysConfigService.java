package cn.hejinyo.jelly.modules.sys.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.sys.model.SysConfig;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 22:16
 */
public interface SysConfigService extends BaseService<SysConfig, Long> {

    /**
     * 根据key，更新value
     */
    public int updateValueByKey(String key, String value);

    /**
     * 根据key，获取配置的value值
     *
     * @param key key
     */
    public String getValue(String key);

    /**
     * 根据key，获取value的Object对象SysConfigServiceImpl
     *
     * @param key   key
     * @param clazz Object对象
     */
    public <T> T getConfigObject(String key, Class<T> clazz);

}
