package com.tail.rpc.model;

import lombok.Data;

/**
 * @author weidong
 * @date Create in 14:53 2018/10/11
 **/
@Data
public class RpcRequest {
    private String id;
    private Class<?> serviceClass;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
