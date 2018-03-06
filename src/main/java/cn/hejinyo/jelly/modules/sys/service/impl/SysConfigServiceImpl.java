package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.common.utils.StringUtils;
import cn.hejinyo.jelly.modules.sys.dao.SysConfigDao;
import cn.hejinyo.jelly.modules.sys.model.SysConfig;
import cn.hejinyo.jelly.modules.sys.service.SysConfigService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 22:16
 */
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigDao, SysConfig, Integer> implements SysConfigService {
    @Autowired
    private RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(SysConfig config) {
        int count = baseDao.save(config);
        redisUtils.set(RedisKeys.getSysConfigKey(config.getKey()), config);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(SysConfig config) {
        int count = baseDao.update(config);
        redisUtils.set(RedisKeys.getSysConfigKey(config.getKey()), config);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateValueByKey(String key, String value) {
        int count = baseDao.updateValueByKey(key, value);
        redisUtils.delete(key);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(Integer[] ids) {
        for (Integer id : ids) {
            SysConfig config = findOne(id);
            redisUtils.delete(config.getKey());
        }
        return baseDao.deleteBatch(ids);
    }


    @Override
    public String getValue(String key) {
        SysConfig config = redisUtils.get(key, SysConfig.class);
        if (config == null) {
            config = baseDao.findByKey(key);
            redisUtils.set(RedisKeys.getSysConfigKey(config.getKey()), config);
        }
        return config.getValue();
    }

    @Override
    public <T> T getConfigObject(String key, Class<T> clazz) {
        String value = getValue(key);
        if (StringUtils.isNotBlank(value)) {
            return new Gson().fromJson(value, clazz);
        }

        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new InfoException("获取参数失败");
        }
    }
}

