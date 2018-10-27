package com.tail.rpc.util;

import com.google.gson.Gson;

/**
 * @author weidong
 * @date create in 21:38 2018/10/24
 **/
public class GsonUtils {

    private final static Gson GSON = new Gson();

    /**
     * 将json转化为对象
     * @param json json字符串
     * @param clazz  序列化对象的class
     * @return json序列化后的对象
     */
    public static <T> T from(String json,Class<T> clazz){
        return GSON.fromJson(json,clazz);
    }

    /**
     * 将对象转化为json
     * @param o 对象
     * @return json字符串
     */
    public static String to(Object o){
        return GSON.toJson(o);
    }

}
