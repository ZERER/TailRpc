package com.tail.rpc.client;


import com.tail.rpc.client.balance.RpcBalance;
import com.tail.rpc.thread.RpcThreadPool;

import java.io.Closeable;
import java.lang.reflect.Proxy;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author weidong
 * @date create in 12:29 2018/10/13
 **/
public class RpcClient implements Closeable {


    private final static ThreadPoolExecutor EXECUTOR = RpcThreadPool.getClientDefaultExecutor();

    private final RpcConfiguration configuration;

    public RpcClient(String zkAddr){
        this();
        configuration.setZkAddr(zkAddr);
    }

    public RpcClient(){
        this.configuration = new RpcConfiguration();
    }

    public RpcClient(RpcConfiguration configuration){
        if (configuration == null){
            throw new NullPointerException("configuration");
        }
       this.configuration = configuration;

    }

    public RpcClient setBanlance(RpcBalance banlance){
        this.configuration.setBalance(banlance);
        return this;
    }

    public RpcClient setZkAddr(String zkAddr){
        this.configuration.setZkAddr(zkAddr);
        return this;
    }

    public RpcClient setRequestTimeOut(long timeOut,TimeUnit unit) {
        this.configuration.setTimeOut( timeOut);
        this.configuration.setTimeUnit( unit);
        return this;
    }

    public RpcClient setServerName(String serverName) {
        this.configuration.setServerName(serverName);
        return this;
    }

    public static void submit(Runnable runnable){
        EXECUTOR.submit(runnable);
    }


    public <T> T create(Class<T> inter){
        return create(inter,null);
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> inter,String serverName){
        return (T) Proxy.newProxyInstance(
                inter.getClassLoader(),
                new Class<?>[]{inter},
                new RpcProxyInvoker<>(inter,configuration,serverName));
    }


    @Override
    public void close(){
        configuration.getConnectManager().close();
        EXECUTOR.shutdown();
    }
}
