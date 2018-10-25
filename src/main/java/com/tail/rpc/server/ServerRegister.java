package com.tail.rpc.server;

import com.tail.rpc.constant.RpcDefaultConfigurationValue;
import com.tail.rpc.model.Information;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Set;

import static com.tail.rpc.constant.RpcDefaultConfigurationValue.ZK_CONNECT_TIME_OUT;
import static com.tail.rpc.constant.RpcDefaultConfigurationValue.ZK_SPILT;

/**
 * @author weidong
 * @date Create in 11:21 2018/10/12
 **/
@Slf4j
public class ServerRegister {


    private CuratorFramework zkClient;

    private final RpcConfiguration configuration;

    private final static String SERVER_NAME = "server";


    public ServerRegister(RpcConfiguration  configuration) {
        this.configuration = configuration;
    }


    /**
     * 1.连接注册中心
     * 2.清理无效服务
     * 3.注册服务
     * @throws Exception
     */
    public void connect() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        zkClient = CuratorFrameworkFactory
                        .builder()
                        .connectString(configuration.getZkAddr())
                        .namespace(RpcDefaultConfigurationValue.NAME_SPACE)
                        .sessionTimeoutMs(ZK_CONNECT_TIME_OUT)
                        .retryPolicy(retryPolicy)
                        .build();

        zkClient.start();



        log.info("zk 正在注册服务");
        Set<String> serverSet = configuration.getServiceMap().keySet();
        for (String service : serverSet){

            Information data = new Information();
            data.setId(configuration.getId());
            data.setAddress(configuration.getServerAddr().getHostName());
            data.setPort(configuration.getServerAddr().getPort());
            data.setRemark(configuration.getRemark());
            data.setWeight(configuration.getWeight());
            data.setServerName(configuration.getServerName());
            zkClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ZK_SPILT+service+ZK_SPILT+SERVER_NAME,data.toDate());
            log.info("create zookeeper node :{}",service);
        }


        log.info("zk 正在清理无效节点");
        List<String> nodes = zkClient.getChildren().forPath(ZK_SPILT);
        for (String node : nodes){
            List<String> server = zkClient.getChildren().forPath(ZK_SPILT + node);
            if(server.isEmpty()){
                log.info("清理无效节点:{}",node);
                zkClient.delete().forPath(ZK_SPILT+node);
            }
        }

    }

    public void close() {
        if (zkClient != null) {
            zkClient.close();
        }
    }

}
