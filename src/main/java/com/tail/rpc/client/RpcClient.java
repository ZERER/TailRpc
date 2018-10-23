package com.tail.rpc.client;


import com.tail.rpc.thread.RpcThreadPool;

import java.io.Closeable;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.tail.rpc.constant.RpcConfiguration.DEFAULT_SERVER_NAME;
import static com.tail.rpc.constant.RpcConfiguration.ZK_ADDR;

/**
 * @author weidong
 * @date create in 12:29 2018/10/13
 **/
public class RpcClient implements Closeable {
    /**
     * 保证一个注册中心一个Connect
     */
    private static final Map<String,RpcConnectManager> ZK_MANAGER = new ConcurrentHashMap<>();

    private final static ThreadPoolExecutor EXECUTOR = RpcThreadPool.getClientDefaultExecutor();

    private long timeOut =  5000;

    private TimeUnit unit = TimeUnit.MILLISECONDS;

    private String serverName = DEFAULT_SERVER_NAME;

    private final String zkAddr;

    public RpcClient(String zkAddr){
        this.zkAddr = zkAddr;
    }

    public RpcClient setRequestTimeOut(long timeOut,TimeUnit unit) {
        this.timeOut = timeOut;
        this.unit =  unit;
        return this;
    }

    public RpcClient setServerName(String serverName) {
        this.serverName = serverName;
        return this;
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
                new RpcProxyInvoker<>(inter,getConnectManager(),timeOut,unit,serverName));
    }

    private RpcConnectManager getConnectManager() {
        //一个注册中心只会有一个RpcConnectManager 可支持多个
        RpcConnectManager connectManager = ZK_MANAGER.get(zkAddr);
        if (connectManager == null) {
            ClientRegister zk = new ClientRegister(zkAddr);
            connectManager = new RpcConnectManager(zk);
            ZK_MANAGER.put(zkAddr, connectManager);
        }
        return connectManager;
    }

    @Override
    public void close(){
        ZK_MANAGER.forEach((zk,manage)-> manage.close());
        EXECUTOR.shutdown();
    }
}
