package com.tail.rpc.client;


import com.tail.rpc.thread.RpcThreadPool;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import static com.tail.rpc.constant.RpcConfiguration.ZK_ADDR;

/**
 * @author weidong
 * @date create in 12:29 2018/10/13
 **/
public class RpcClient {

    private RpcConnectManager connectManager;

    private static final Map<String,RpcConnectManager> ZK_MANAGER = new ConcurrentHashMap<>();

    private final static ThreadPoolExecutor EXECUTOR = RpcThreadPool.getClientDefaultExecutor();

    public RpcClient(String zkAddr){
        RpcConnectManager connectManager = ZK_MANAGER.get(zkAddr);
        if (connectManager == null){
            ClientRegister zk = new ClientRegister(zkAddr);
            connectManager = new RpcConnectManager(zk);
            ZK_MANAGER.put(zkAddr,connectManager);
        }
        this.connectManager = connectManager;
    }

    public RpcClient(){
        this(ZK_ADDR);
    }

    public static void submit(Runnable runnable){
        EXECUTOR.submit(runnable);
    }


    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> inter){
        return (T) Proxy.newProxyInstance(
                inter.getClassLoader(),
                new Class<?>[]{inter},
                //new RpcProxyInvoker<>(inter,zkAddr,serverNode)
                new RpcProxyInvoker<>(inter,connectManager)
        );
    }

    public void close(){
        ZK_MANAGER.forEach((zk,manage)->{
           manage.close();
        });
        EXECUTOR.shutdown();
    }
}
