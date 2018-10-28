package com.tail.rpc.client;


import com.tail.rpc.client.async.proxy.AsyncProxy;
import com.tail.rpc.client.async.proxy.AsyncProxyImpl;
import com.tail.rpc.client.balance.RpcBalance;

import java.io.Closeable;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author weidong
 * @date create in 12:29 2018/10/13
 **/
public class RpcClient implements Closeable {


    private final RpcConfiguration configuration;

    public RpcClient(String zkAddr) {
        this();
        configuration.setZkAddr(zkAddr);
    }

    public RpcClient() {
        this.configuration = new RpcConfiguration();
    }

    public RpcClient(RpcConfiguration configuration) {
        if (configuration == null) {
            throw new NullPointerException("configuration");
        }
        this.configuration = configuration;

    }

    public RpcClient setBanlance(RpcBalance banlance) {
        this.configuration.setBalance(banlance);
        return this;
    }

    public RpcClient setZkAddr(String zkAddr) {
        this.configuration.setZkAddr(zkAddr);
        return this;
    }

    public RpcClient setRequestTimeOut(long timeOut, TimeUnit unit) {
        this.configuration.setTimeOut(timeOut);
        this.configuration.setTimeUnit(unit);
        return this;
    }

    public RpcClient setServerName(String serverName) {
        this.configuration.setServerName(serverName);
        return this;
    }


    public <T> T create(Class<T> inter) {
        return create(inter, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> tClass, String serverName) {
        return (T) Proxy.newProxyInstance(
                tClass.getClassLoader(),
                new Class<?>[]{tClass},
                new RpcProxyInvoker<>(tClass, configuration, serverName));
    }

    public AsyncProxy createAsyncProxy(Class<?> tClass) {
        return new AsyncProxyImpl(tClass, configuration);
    }

    @Override
    public void close() {
        configuration.getConnectManager().close();
    }
}
