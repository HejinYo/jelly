package cn.hejinyo.jelly.modules.wechat.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.wechat.model.WechatJoke;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WechatJokeDao extends BaseDao<WechatJoke, Integer> {
}