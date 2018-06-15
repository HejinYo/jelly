package cn.hejinyo.jelly.common.utils;

import cn.hejinyo.jelly.common.consts.Constant;

/**
 * Redis所有Keys
 */
public class RedisKeys {
    private static final String PREFIX = "jelly";
    public static final String USER_TOKEN = "token";
    public static final String USER_PERM = "perm";
    public static final String USER_ROLE = "role";
    public static final String USER_CUR_DEPT = Constant.Dept.CUR_DEPT.getValue();
    public static final String USER_SUB_DEPT = Constant.Dept.SUB_DEPT.getValue();
    public static final String USER_ALL_DEPT = Constant.Dept.ALL_DEPT.getValue();

    private static String buildKey(Object... key) {
        StringBuilder sb = new StringBuilder();
        for (Object o : key) {
            sb.append(String.valueOf(o)).append(":");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * 配置参数
     */
    public static String storeConfig() {
        return buildKey(PREFIX, "store", "config");
    }

    /**
     * 用户信息存放redis map key
     */
    public static String storeUser(Object userId) {
        return buildKey(PREFIX, "store", "user", userId);
    }

}
