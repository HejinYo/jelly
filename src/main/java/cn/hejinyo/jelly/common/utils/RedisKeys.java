package cn.hejinyo.jelly.common.utils;

/**
 * Redis所有Keys
 */
public class RedisKeys {

    /**
     * 配置参数
     */
    public static String getSysConfigKey(String key) {
        return "sys:config:" + key;
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
