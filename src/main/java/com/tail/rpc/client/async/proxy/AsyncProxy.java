package com.tail.rpc.client.async.proxy;

import com.tail.rpc.client.async.RpcFuture;

/**
 * @author weidong
 * @date create in 11:55 2018/10/28
 **/
public interface AsyncProxy {

    /**
     * 异步调用rpc服务
     * @param method 方法名
     * @param paramters 方法参数
     * @return RpcFuture
     */
    RpcFuture asyncInvoke(String method,Object... paramters);
    /**
     * 异步调用rpc服务
     * @param serverName 服务名称
     * @param method 方法名
     * @param paramters 方法参数
     * @return RpcFuture
     */
    RpcFuture asyncInvokeWithServerName(String serverName,String method,Object... paramters);
}
