package cn.hejinyo.jelly.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import java.util.Map;

public final class JsonUtils {

    private static final Gson GSON = new Gson();

    private JsonUtils() {
        throw new Error("工具类不能实例化！");
    }

    public static Gson gson() {
        return GSON;
    }

    /**
     * 序列化
     */
    public static String toJSONString(Object entity) {
        return JSON.toJSONString(entity);
    }

    /**
     * 反序列化
     */
    public static <T> T toObject(String json, Class<T> c) {
        return JSON.parseObject(json, c);
    }

    /**
     * 转map
     */
    public static Map toMap(String json) {
        return (Map) JSON.parse(json);
    }


}