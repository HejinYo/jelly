package cn.hejinyo.jelly.modules.applets.service;

import cn.hejinyo.jelly.common.base.BaseService;
import cn.hejinyo.jelly.modules.applets.model.WeUser;

/**
 * @author : heshuangshuang
 * @date : 2018/3/10 17:16
 */
public interface WeUserService extends BaseService<WeUser, Integer> {

    /**
     * 根据openId查找一个用户
     *
     * @param openId
     * @return
     */
    WeUser findOneByOpenId(String openId);
}
