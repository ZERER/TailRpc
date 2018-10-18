package com.tail.rpc.model;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author weidong
 * @date Create in 14:53 2018/10/11
 **/
@Data
public class RpcRequest {
    /**
     * 请求id
     */
    private String id;
    /**
     * 请求的class对象
     */
    private Class<?> service;
    /**
     * 方法名
     */
    private Method method;
    /**
     * 请求参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 请求参数值
     */
    private Object[] parameters;
}
