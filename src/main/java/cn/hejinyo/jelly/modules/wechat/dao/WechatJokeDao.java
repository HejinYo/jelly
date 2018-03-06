package cn.hejinyo.jelly.modules.wechat.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.wechat.model.WechatJoke;
import org.apache.ibatis.annotations.Mapper;

/**
 * wechat_joke 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2018/03/06 23:03
 */
@Mapper
public interface WechatJokeDao extends BaseDao<WechatJoke, Integer> {
}