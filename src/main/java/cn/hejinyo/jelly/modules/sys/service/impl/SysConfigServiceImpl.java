package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.sys.dao.SysConfigDao;
import cn.hejinyo.jelly.modules.sys.dao.SysConfigOptionDao;
import cn.hejinyo.jelly.modules.sys.model.SysConfigEntity;
import cn.hejinyo.jelly.modules.sys.model.SysConfigOptionEntity;
import cn.hejinyo.jelly.modules.sys.service.SysConfigService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 22:16
 */
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigDao, SysConfigEntity, Integer> implements SysConfigService {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SysConfigOptionDao sysConfigOptionDao;

    /**
     * 根据code获取系统配置信息
     */
    @Override
    public String getConfig(String code) {
        Optional<SysConfigEntity> config = Optional.ofNullable(baseDao.getConfig(code));
        return config.map(SysConfigEntity::getValue).orElse(null);
    }

    /**
     * 根据code获取系统配置信息
     */
    @Override
    public <T> T getConfig(String code, Class<T> clazz) {
        //缓存中存在直接返回
        T config = redisUtils.hget(RedisKeys.storeConfig(), code, clazz);
        if (config != null) {
            return config;
        }

        //不存在从数据库拿
        String configStr = getConfig(code);
        if (configStr != null) {
            config = JsonUtil.fromJson(configStr, clazz);
            redisUtils.hset(RedisKeys.storeConfig(), code, config);
        }
        return config;
    }


    /**
     * 配置目录分页查询
     */
    @Override
    public List<SysConfigEntity> findPage(PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize(), pageQuery.getOrder());
        List<SysConfigEntity> list = baseDao.findPage(pageQuery);
        list.forEach(value -> {
            value.setOptionList(sysConfigOptionDao.findOptionListByCode(value.getCode()));
        });
        return list;
    }

    /**
     * 保存配置目录
     */
    @Override
    public int save(SysConfigEntity config) {
        SysConfigEntity newConfig = PojoConvertUtil.convert(config, SysConfigEntity.class);
        newConfig.setCreateId(ShiroUtils.getUserId());
        newConfig.setStatus(Constant.Status.NORMAL.getValue());
        //清除缓存
        redisUtils.hdel(RedisKeys.storeConfig(), config.getCode());
        return baseDao.save(newConfig);
    }

    /**
     * 修改配置目录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Integer configId, SysConfigEntity config) {
        SysConfigEntity newConfig = PojoConvertUtil.convert(config, SysConfigEntity.class);
        newConfig.setConfigId(configId);
        newConfig.setUpdateId(ShiroUtils.getUserId());
        SysConfigEntity oldConfig = baseDao.findOne(configId);

        //修改CODE,配置属性同步修改
        if (newConfig.getCode() != null && !oldConfig.getCode().equals(newConfig.getCode())) {
            sysConfigOptionDao.updateCode(oldConfig.getCode(), newConfig.getCode());
        }

        //清除缓存
        redisUtils.hdel(RedisKeys.storeConfig(), config.getCode());

        return baseDao.update(newConfig);
    }

    /**
     * 更新配置选项
     */
    @Override
    public int updateOptionId(Integer configId, Integer optionId) {
        SysConfigEntity oldConfig = baseDao.findOne(configId);
        SysConfigEntity config = new SysConfigEntity();
        config.setConfigId(configId);
        config.setOptionId(optionId);
        int count = baseDao.update(config);
        if (count > 0) {
            //清除缓存
            redisUtils.hdel(RedisKeys.storeConfig(), oldConfig.getCode());
        }
        return count;
    }

    /**
     * 删除配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        SysConfigEntity oldConfig = baseDao.findOne(id);
        int count = baseDao.delete(id);
        if (count > 0) {
            // 通过Code删除配置属性
            sysConfigOptionDao.deleteByCode(oldConfig.getCode());
            //清除缓存
            redisUtils.hdel(RedisKeys.storeConfig(), oldConfig.getCode());
        }
        return count;
    }

    /**
     * 根据配置code获取配置值列表
     */
    @Override
    public List<SysConfigOptionEntity> getOptionListByCode(String code) {
        return sysConfigOptionDao.findOptionListByCode(code);
    }

    /**
     * 保存配置属性
     */
    @Override
    public int saveOption(SysConfigOptionEntity config) {
        config.setCreateId(ShiroUtils.getUserId());
        return sysConfigOptionDao.save(config);
    }

    /**
     * 修改配置属性
     */
    @Override
    public int updateOption(Integer optionId, SysConfigOptionEntity config) {
        config.setOptionId(optionId);
        config.setUpdateId(ShiroUtils.getUserId());
        int count = sysConfigOptionDao.update(config);
        if (count > 0) {
            //清除缓存
            redisUtils.hdel(RedisKeys.storeConfig(), config.getCode());
        }
        return count;
    }

    /**
     * 删除配置属性
     */
    @Override
    public int deleteBatchOption(Integer optionId) {
        Optional<SysConfigOptionEntity> configOption = Optional.ofNullable(sysConfigOptionDao.findOne(optionId));
        return configOption.map(sc -> {
            // 检查是否有配置正在使用
            SysConfigEntity config = baseDao.getConfig(sc.getCode());
            if (optionId.equals(Optional.ofNullable(config).map(SysConfigEntity::getOptionId).orElse(null))) {
                throw new InfoException("配置属性被使用，不允许删除");
            }
            //清除缓存
            redisUtils.hdel(RedisKeys.storeConfig(), sc.getCode());
            return sysConfigOptionDao.delete(optionId);
        }).orElse(0);
    }
}

