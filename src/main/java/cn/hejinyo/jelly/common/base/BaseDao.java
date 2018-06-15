package cn.hejinyo.jelly.common.base;

import cn.hejinyo.jelly.common.utils.PageQuery;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/26 18:28
 */
public interface BaseDao<T, ID extends Serializable> {

    /**
     * 增加
     */
    int save(T entity);

    int save(Map<String, Object> map);

    int saveBatch(List<T> list);


    /**
     * 更新
     */
    int update(T entity);

    int update(Map<String, Object> map);

    /**
     * 删除给定主键的记录
     */
    int delete(ID id);

    int delete(Map<String, Object> map);

    int deleteBatch(ID[] ids);

    int deleteList(List<T> entity);


    /**
     * 根据主键查找一条记录
     */
    T findOne(ID id);

    T findOne(T entity);

    T findOne(Map<String, Object> map);

    List<T> findAllList();

    List<T> findList(T entity);

    List<T> findList(Map<String, Object> map);

    List<T> findPage(PageQuery pageQuery);

    /**
     * 实体属性的记录数量
     */
    int count(T entity);

    /**
     * 实体是否存在
     */
    boolean exsit(T entity);

}
