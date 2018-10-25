package com.tail.rpc.client;

import com.tail.rpc.client.service.ServiceBean;
import com.tail.rpc.constant.RpcDefaultConfigurationValue;
import com.tail.rpc.model.Information;
import com.tail.rpc.util.ProtostuffUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tail.rpc.constant.RpcDefaultConfigurationValue.ZK_CONNECT_TIME_OUT;
import static com.tail.rpc.constant.RpcDefaultConfigurationValue.ZK_SPILT;

/**
 * @author weidong
 * @date create in 14:20 2018/10/13
 **/
@Slf4j
public class ClientRegister {


    private CuratorFramework zkClient;

    private String zkAddr;

    public ClientRegister(String zkAddr){
        this.zkAddr = zkAddr;
    }

    public void connect(){
        //连接zookeeper
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        zkClient = CuratorFrameworkFactory
                .builder()
                .connectString(zkAddr)
                .namespace(RpcDefaultConfigurationValue.NAME_SPACE)
                .sessionTimeoutMs(ZK_CONNECT_TIME_OUT)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
    }

    /**
     * 从注册中心获取服务
     * @param serverName 注册服务名字
     * @return 注册服务
     */
    public List<ServiceBean> getServer(String serverName) {
        try {

            List<String> serverNames = zkClient.getChildren().forPath(ZK_SPILT+serverName);
            List<ServiceBean> values = new ArrayList<>(serverNames.size());
            for (String name : serverNames){
                ServiceBean bean = new ServiceBean(ProtostuffUtils.deserializer(zkClient.getData().forPath(ZK_SPILT+serverName+ZK_SPILT+name),Information.class));
                values.add(bean);
            }
            return values;
        } catch (Exception e) {
            log.error("zookeeper获取服务失败");
            return Collections.emptyList();

        }
    }

    public void close() {
        if (zkClient != null){
            zkClient.close();
        }
    }
}
