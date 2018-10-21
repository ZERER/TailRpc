package com.tail.rpc.server;

import com.tail.rpc.annotation.RpcService;
import com.tail.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.tail.rpc.constant.RpcConfiguration.ZK_SPILT;

/**
 * @author weidong
 * @date Create in 15:32 2018/10/11
 **/
@Slf4j
public class ServiceCenter {

    private final Map<String,Object> serverMap = new HashMap<>();


    public boolean containsServer(String interfaceClass){
        return serverMap.containsKey(interfaceClass);
    }

    public void addService(Object server,String serverName){
        if (server == null){
            throw new NullPointerException("server");
        }

        Class<?> clazz = server.getClass();
        RpcService rpcService = hasRpcAnnotation(clazz);
        Class<?>[] interfaceClass =  !rpcService.Value().equals(Class.class)
                ? new Class[]{rpcService.Value()}
                : clazz.getInterfaces();
        if (interfaceClass.length != 1){
            throw new RuntimeException("不合理的服务提供者");
        }
        addService(interfaceClass[0], server,serverName);
    }


    public void addService(Class<?> interfaceClass, Object server,String serverName) {
        if (server == null || interfaceClass == null){
            throw new NullPointerException("server or interface");
        }
        String fullServerName = interfaceClass.getName()+ZK_SPILT+serverName;
        hasRpcAnnotation(server.getClass());
        if (containsServer(fullServerName)) {
            throw new RpcException("服务已存在");
        }
        serverMap.put(fullServerName, server);
    }

    public int size(){
        return serverMap.size();
    }

    public Set<String> getServiceName(){
        return serverMap.keySet();
    }

    public Object getService(String key){
        return serverMap.get(key);
    }

    private RpcService hasRpcAnnotation(Class<?> server){
        RpcService rpcService = server.getAnnotation(RpcService.class);
        if (rpcService == null){
            throw new RpcException("找不到注解@RpcServer");
        }
        return rpcService;
    }

    public static ServiceCenter instance(){
        return ServiceCenterClass.serviceCenter;
    }
    private ServiceCenter(){}

    static class ServiceCenterClass{
        static ServiceCenter serviceCenter = new ServiceCenter();
    }
}
