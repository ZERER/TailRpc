package com.tail.rpc.util;

import com.google.gson.Gson;

/**
 * @author weidong
 * @date create in 21:38 2018/10/24
 **/
public class GsonUtils {

    private final static Gson GSON = new Gson();

    public static <T> T from(String json,Class<T> clazz){
        return GSON.fromJson(json,clazz);
    }

    public static String to(Object o){
        return GSON.toJson(o);
    }

}
