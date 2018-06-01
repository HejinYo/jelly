package cn.hejinyo.jelly.common.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 列表递归成树数据
 *
 * @author : heshuangshuang
 * @date : 2018/5/22 9:20
 */
public class RecursionUtil {
    private final static String DEF_ID_NAME = "getId";
    private final static String DEF_PID_NAME = "getParentId";
    private final static String DEF_CHILDREN_NAME = "setChildren";


    /**
     * 将列表递归成树，默认方法 "getId"，"getParentId"，"setChildren"
     */
    public static <T, K> List<T> tree(boolean isRoot, Class<T> clazz, List<T> list, List<K> parentIdList) {
        return tree(isRoot, clazz, DEF_ID_NAME, DEF_PID_NAME, list, parentIdList);
    }

    /**
     * 将列表递归成树，默认方法  "getParentId"，"setChildren"
     */
    public static <T, K> List<T> tree(boolean isRoot, Class<T> clazz, String getIdName, List<T> list, List<K> parentIdList) {
        return tree(isRoot, clazz, getIdName, DEF_PID_NAME, list, parentIdList);
    }

    /**
     * 将列表递归成树，默认方法  "setChildren"
     */
    public static <T, K> List<T> tree(boolean isRoot, Class<T> clazz, String getIdName, String getPidName, List<T> list, List<K> parentIdList) {
        return tree(isRoot, clazz, getIdName, getPidName, DEF_CHILDREN_NAME, list, parentIdList);
    }

    /**
     * 将列表递归成树
     *
     * @param isRoot          是否显示根节点
     * @param clazz           遍历列表对象类型
     * @param getIdName       获取对象主键方法名称，反射获得
     * @param getPidName      获取对象父节点主键方法名称，反射获得
     * @param setChildernName 设置对象子节点方法名称，反射获得
     * @param list            需要遍历的列表
     * @param parentIdList    父节点编号列表
     * @param <T>             遍历列表对象的泛型
     * @param <K>             遍历列表对象主键泛型
     */
    @SuppressWarnings("unchecked")
    public static <T, K> List<T> tree(boolean isRoot, Class<T> clazz, String getIdName, String getPidName, String setChildernName, List<T> list, List<K> parentIdList) {
        List<T> result = new ArrayList<>();
        try {

            Method getId = clazz.getMethod(getIdName);
            Method getParentId = clazz.getMethod(getPidName);
            Method setChildren = clazz.getMethod(setChildernName, List.class);

            for (T value : list) {
                // 根节点可能会有多个
                for (K rId : parentIdList) {

                    // 获取对象主键方法
                    K id = (K) getId.invoke(value);
                    // 获取对象父节点方法
                    K pid = (K) getParentId.invoke(value);

                    //如果是true，需要将根节点加入树节点，否则递归查询子节点
                    boolean checkRelation = isRoot ? rId.equals(id) : rId.equals(pid);

                    //!value.getRoleId().equals(value.getParentId()) 杜绝死循环情况
                    if (checkRelation && !id.equals(pid)) {
                        setChildren.invoke(value, tree(false, clazz, getIdName, list, new ArrayList<>(Collections.singletonList(id))));
                        result.add(value);
                        list.remove(value);
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取列表+=树数据
     */
    public static <T, K> HashMap<String, List<T>> listTree(boolean isRoot, Class<T> clazz, String getIdName, List<T> list, List<K> parentIdList) {
        HashMap<String, List<T>> map = new HashMap<>(2);
        map.put("list", list);
        map.put("tree", tree(isRoot, clazz, getIdName, DEF_PID_NAME, new CopyOnWriteArrayList<>(list), parentIdList));
        return map;
    }

    /**
     * 递归获取节点下所有子节点
     *
     * @param isRoot       是否显示根节点
     * @param list         需要遍历的列表
     * @param parentIdList 父节点编号列表
     */
    @SuppressWarnings("unchecked")
    public static <T, K> List<K> list(List<K> allList, Class<T> clazz, String getIdName, boolean isRoot, List<T> list, List<K> parentIdList) {

        try {

            Method getId = clazz.getMethod(getIdName);
            Method getParentId = clazz.getMethod(DEF_PID_NAME);

            for (T value : list) {
                // 根节点可能会有多个
                for (K rId : parentIdList) {

                    // 获取对象主键方法
                    K id = (K) getId.invoke(value);
                    // 获取对象父节点方法
                    K pid = (K) getParentId.invoke(value);

                    //如果是true，需要将根节点加入树节点，否则递归查询子节点
                    boolean checkRelation = isRoot ? rId.equals(id) : rId.equals(pid);

                    //!value.getRoleId().equals(value.getParentId()) 杜绝死循环情况
                    if (checkRelation && !id.equals(pid)) {
                        list(allList, clazz, getIdName, false, list, new ArrayList<>(Collections.singletonList(id)));
                        allList.add(id);
                        list.remove(value);
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return allList;
    }


}
