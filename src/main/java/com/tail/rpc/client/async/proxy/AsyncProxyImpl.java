package com.tail.rpc.client.async.proxy;

import com.tail.rpc.client.RpcConfiguration;
import com.tail.rpc.client.async.RpcFuture;
import com.tail.rpc.model.RpcRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author weidong
 * @date create in 11:57 2018/10/28
 **/
public class AsyncProxyImpl implements AsyncProxy {

    /**
     * 异步调用的类
     */
    private final Class<?> proxyClass;

    private final RpcConfiguration configuration;

    public AsyncProxyImpl(Class<?> proxyClass, RpcConfiguration configuration){
        this.proxyClass = proxyClass;
        this.configuration = configuration;
    }

    @Override
    public RpcFuture asyncInvoke(String method, Object... paramters) {
       return asyncInvokeWithServerName(null,method,paramters);
    }

    @Override
    public RpcFuture asyncInvokeWithServerName(String serverName,String method, Object... paramters) {
        return doInvoke(StringUtils.isNotEmpty(serverName) ? serverName : configuration.getServerName(),method,paramters);
    }

    private RpcFuture doInvoke(String serverName, String method, Object[] paramters) {
        RpcRequest request = new RpcRequest();
        request.setServerName(serverName);
        request.setClassName(proxyClass.getSimpleName());
        request.setUnit(configuration.getTimeUnit());
        request.setTimeOut(configuration.getTimeOut());
        request.setId(UUID.randomUUID().toString());
        request.setMethodName(method);
        request.setParameters(paramters);
        Class<?>[] paramterTypes = new Class<?>[paramters.length];
        for (int i = 0 ;i < paramters.length; i++){
            paramterTypes[i] = paramters[i].getClass();
        }
        request.setParameterTypes(paramterTypes);
        return configuration.getConnectManager().handle(request);
    }


}
