package com.tail.rpc.client;

import com.tail.rpc.client.async.Sync;
import com.tail.rpc.client.balance.DefaultBalance;
import com.tail.rpc.client.balance.RpcBalance;
import com.tail.rpc.exception.RpcException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.tail.rpc.constant.RpcDefaultConfigurationValue.*;

/**
 * @author weidong
 * @date create in 20:12 2018/10/24
 **/
@Data
public class RpcConfiguration {

    /**
     * 已连接的zookeeper地址
     */
    public static final List<String> ZK_ADDRS = new ArrayList<>();
    /**
     * 同步组件
     */
    private static Sync sync = new Sync(false);
    /**
     * 超时时长
     */
    private long timeOut =  TIME_OUT;
    /**
     * 时长单位
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    /**
     * 生产者名字
     */
    private String serverName = DEFAULT_SERVER_NAME;
    /**
     * zookeeper地址
     */
    private String zkAddr = DEFAULT_ZK_ADDRESS;
    /**
     * 连接管理器
     */
    private RpcConnectManager connectManager;
    /**
     * 负载均衡策略
     */
    private RpcBalance balance = new DefaultBalance();

    public RpcConnectManager getConnectManager() {
        if (connectManager == null){
            sync.acquire(1);
            try {
                if (ZK_ADDRS.contains(zkAddr)) {
                    throw new RpcException("zkaddr 已注册");
                }
                connectManager =  new RpcConnectManager(new ClientRegister(zkAddr),balance);
            } catch (Exception e) {

            } finally {
                sync.release(1);
            }
        }
        return connectManager;
    }
}
