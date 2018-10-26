package com.tail.rpc.client;

import com.tail.rpc.client.async.Sync;
import com.tail.rpc.exception.RpcException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
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

    public static final List<String> ZK_ADDRS = new ArrayList<>();

    private static Sync sync = new Sync();

    private long timeOut =  TIME_OUT;

    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    private String serverName = DEFAULT_SERVER_NAME;

    private String zkAddr = DEFAULT_ZK_ADDR;

    private RpcConnectManager connectManager;

    public RpcConnectManager getConnectManager() {
        if (connectManager == null){
            sync.acquire(1);
            try {
                if (ZK_ADDRS.contains(zkAddr)) {
                    throw new RpcException("zkaddr 已注册");
                }
                connectManager =  new RpcConnectManager(new ClientRegister(zkAddr));
            } catch (Exception e) {

            } finally {
                sync.release(1);
            }
        }
        return connectManager;
    }
}
