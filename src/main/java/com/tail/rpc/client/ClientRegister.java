package com.tail.rpc.client;

import com.tail.rpc.constant.RpcConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.*;

import static com.tail.rpc.constant.RpcConfiguration.ZK_CONNECT_TIME_OUT;
import static com.tail.rpc.constant.RpcConfiguration.ZK_SPILT;

/**
 * @author weidong
 * @date create in 14:20 2018/10/13
 **/
@Slf4j
public class ClientRegister {


    private String data;

    private CuratorFramework zkClient;

    private ServiceDiscovery serviceDiscovery = ServiceDiscovery.instance();

    public ClientRegister(String zkAddr){

        if (StringUtils.isEmpty(zkAddr)){
            throw new NullPointerException("zkAddr");
        }
        connect(zkAddr);

    }

    public void connect(String zkAddr){
        //连接zookeeper
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        zkClient = CuratorFrameworkFactory
                .builder()
                .connectString(zkAddr)
                .namespace(RpcConfiguration.NAME_SPACE)
                .sessionTimeoutMs(ZK_CONNECT_TIME_OUT)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
    }

    /**
     * 从注册中心获取服务
     * @param serverName 注册服务名字
     * @return
     */
    public List<InetSocketAddress> getServer(String serverName) {
        try {

            String serverNode = ZK_SPILT+serverName;
            List<String> server = zkClient.getChildren().forPath(serverNode);
            List<InetSocketAddress> socketAddresses =  warp(server);
            serviceDiscovery.put(serverNode, socketAddresses);
            return socketAddresses;
        } catch (Exception e) {
            log.error("zookeeper获取服务失败");
            return Collections.emptyList();

        }
    }

    private List<InetSocketAddress> warp(List<String> serverNodes) {
        List<InetSocketAddress> socketAddresses = new LinkedList<>();

        serverNodes.forEach(serverNode->{
            String[] socketAddress = StringUtils.split(":");
            socketAddresses.add(new InetSocketAddress(socketAddress[0],Integer.valueOf(socketAddress[1])));
        });
        return socketAddresses;
    }
}
