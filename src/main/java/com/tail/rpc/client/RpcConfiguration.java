package com.tail.rpc.client;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.tail.rpc.constant.RpcDefaultConfigurationValue.DEFAULT_SERVER_NAME;
import static com.tail.rpc.constant.RpcDefaultConfigurationValue.DEFAULT_ZK_ADDR;
import static com.tail.rpc.constant.RpcDefaultConfigurationValue.TIME_OUT;

/**
 * @author weidong
 * @date create in 20:12 2018/10/24
 **/
@Data
public class RpcConfiguration {

    /**
     * 保证一个注册中心一个Connect
     */
    public static final Map<String,RpcConnectManager> ZK_MANAGER = new ConcurrentHashMap<>();

    private long timeOut =  TIME_OUT;

    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    private String serverName = DEFAULT_SERVER_NAME;

    private String zkAddr = DEFAULT_ZK_ADDR;

    public RpcConnectManager getConnectManager() {
        //一个注册中心只会有一个RpcConnectManager 可支持多个
        RpcConnectManager connectManager = ZK_MANAGER.get(zkAddr);
        if (connectManager == null) {
            ClientRegister zk = new ClientRegister(zkAddr);
            connectManager = new RpcConnectManager(zk);
            ZK_MANAGER.put(zkAddr, connectManager);
        }
        return connectManager;
    }
}
