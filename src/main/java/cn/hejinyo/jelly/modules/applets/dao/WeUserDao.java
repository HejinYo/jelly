package cn.hejinyo.jelly.modules.applets.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.applets.model.WeUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * w_user 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/03/10 17:14
 */
@Mapper
public interface WeUserDao extends BaseDao<WeUser, Integer> {
    /**
     * 根据openId查找一个用户
     *
     * @param openId
     * @return
     */
    WeUser findOneByOpenId(String openId);
}