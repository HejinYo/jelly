package cn.hejinyo.jelly.common.utils;

/**
 * Redis所有Keys
 */
public class RedisKeys {
    private static final String PREFIX = "jelly";
    public static final String USER_TOKEN = "token";
    public static final String USER_PERM = "perm";
    public static final String USER_ROLE = "role";
    public static final String USER_DEPT = "dept";

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
    public static String getSysConfigKey(String key) {
        return "sys:config:" + key;
    }

    /**
     * 用户信息存放redis map key
     */
    public static String storeUser(Object userId) {
        return buildKey(PREFIX, "store", "user", userId);
    }


    /**
     * 登录token
     */
    public static String getTokenCacheKey(String key) {
        return "sys:tokenCache:" + key;
    }

    /**
     * 用户权限
     */
    public static String getAuthCacheKey(String key) {
        return "sys:authCache:" + key;
    }

    /**
     * 登录次数过多锁定
     */
    public static String getLoginRecordCacheKey(String key) {
        return "sys:recordCache:" + key;
    }
}
