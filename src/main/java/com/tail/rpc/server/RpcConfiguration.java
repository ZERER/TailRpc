package com.tail.rpc.server;

import com.tail.rpc.annotation.RpcService;
import com.tail.rpc.constant.RpcDefaultConfigurationValue;
import com.tail.rpc.exception.RpcException;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.tail.rpc.constant.RpcDefaultConfigurationValue.*;

/**
 * @author weidong
 * @date create in 20:12 2018/10/24
 **/
@Data
public class RpcConfiguration {

    private final String id = UUID.randomUUID().toString().replace("_","");

    /**
     * 服务提供者服务名
     * example HelloServer
     */
    private String serverName = DEFAULT_SERVER_NAME;

    /**
     * 注册中心地址,默认为RpcConfiguration.ZK_ADDR
     * @see RpcDefaultConfigurationValue
     */
    private String zkAddr = DEFAULT_ZK_ADDRESS;

    /**
     * 服务提供者地址
     */
    private InetSocketAddress serverAddr = DEFAULT_INET_ADDR;
    /**
     * 服务提供者权重
     * example 0
     */
    private Integer weight = DEFAULT_WEIGHT;
    /**
     * 备注
     */
    private String remark;
    /**
     * 服务列表
     */
    private final Map<String,Object> serviceMap = new HashMap<>();


    public void addService(Object service){
        addService(service.getClass().getSimpleName(),service);
    }

    public void addService(String serviceName,Object service){
        RpcService rpcService = hasRpcAnnotation(service.getClass());
        Class<?>[] interfaceClass =  !rpcService.Value().equals(Class.class)
                ? new Class[]{rpcService.Value()}
                : service.getClass().getInterfaces();
        if (interfaceClass.length != 1){
            throw new RuntimeException("不合理的服务提供者");
        }
        serviceMap.put(interfaceClass[0].getSimpleName(),service);
    }

    public int getServiceSize(){
        return serviceMap.size();
    }

    private RpcService hasRpcAnnotation(Class<?> server){
        RpcService rpcService = server.getAnnotation(RpcService.class);
        if (rpcService == null){
            throw new RpcException("找不到注解@RpcServer");
        }
        return rpcService;
    }

}
