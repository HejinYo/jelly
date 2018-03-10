package cn.hejinyo.jelly.modules.applets.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.modules.applets.dao.WeUserDao;
import cn.hejinyo.jelly.modules.applets.model.WeUser;
import cn.hejinyo.jelly.modules.applets.service.WeUserService;
import org.springframework.stereotype.Service;

/**
 * @author : heshuangshuang
 * @date : 2018/3/10 17:17
 */
@Service
public class WeUserServiceImpl extends BaseServiceImpl<WeUserDao, WeUser, Integer> implements WeUserService {

    /**
     * 根据openId查找一个用户
     *
     * @param openId
     * @return
     */
    @Override
    public WeUser findOneByOpenId(String openId) {
        return baseDao.findOneByOpenId(openId);
    }
}
