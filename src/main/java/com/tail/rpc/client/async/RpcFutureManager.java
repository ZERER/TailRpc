package com.tail.rpc.client.async;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理所有RpcFuture
 * @author weidong
 * @date create in 20:58 2018/10/16
 **/
public class RpcFutureManager {

    public ConcurrentHashMap<String,RpcFuture> resultMap = new ConcurrentHashMap<>();

    public RpcFuture getRpcFuture(String requestId){
        return resultMap.get(requestId);
    }

    public void putRpcFuture(String requestId,RpcFuture future){
        resultMap.put(requestId,future);
    }

    public void removeFuture(String requestId){
        resultMap.remove(requestId);
    }

    /**
     * 单例模式
     */
    private RpcFutureManager(){}

    public static RpcFutureManager instance(){
        return RpcFutureManagerClass.manager;
    }

    static class RpcFutureManagerClass{
        static RpcFutureManager manager = new RpcFutureManager();
    }

}
