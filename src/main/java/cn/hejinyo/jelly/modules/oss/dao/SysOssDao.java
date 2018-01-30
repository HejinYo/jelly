package cn.hejinyo.jelly.modules.oss.dao;

import cn.hejinyo.jelly.common.base.BaseDao;
import cn.hejinyo.jelly.modules.oss.model.SysOss;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件上传
 *
 * @author :heshuangshuang
 * @date :2018/1/20 10:10
 */
@Mapper
public interface SysOssDao extends BaseDao<SysOss, Long> {
}
