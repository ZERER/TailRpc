package com.tail.rpc.client;

import com.tail.rpc.model.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author weidong
 * @date create in 13:05 2018/10/13
 **/
@Slf4j
public class RpcProxyInvoker<T> implements InvocationHandler {

    private RpcRequest request = new RpcRequest();

    private RpcConnectManager manager;


    public RpcProxyInvoker(Class<T> inter, RpcConnectManager connectManager,long timeOut,TimeUnit unit,String serverName) {
        this.manager = connectManager;
        this.request.setId(UUID.randomUUID().toString());
        this.request.setTimeOut(timeOut);
        this.request.setUnit(unit);
        this.request.setClassName(inter.getSimpleName());
        this.request.setServerName(serverName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.request.setMethodName(method.getName());
        this.request.setParameterTypes(method.getParameterTypes());
        this.request.setParameters(args);
        log.info("调用rpc服务,请求参数:{}",request.toString());
        return manager.handle(request);
    }


}
