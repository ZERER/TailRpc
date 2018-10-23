package com.tail.rpc.server;

import com.tail.rpc.constant.RpcConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Set;

import static com.tail.rpc.constant.RpcConfiguration.STR_SPILT;
import static com.tail.rpc.constant.RpcConfiguration.ZK_CONNECT_TIME_OUT;
import static com.tail.rpc.constant.RpcConfiguration.ZK_SPILT;

/**
 * @author weidong
 * @date Create in 11:21 2018/10/12
 **/
@Slf4j
public class ServerRegister {

    private final String zkAddr;

    //private final String serverNode;

    private String data;

    private CuratorFramework zkClient;

    private ServiceCenter serviceList = ServiceCenter.instance();

    private final static String SERVER_NAME = "server";


    public ServerRegister(String registerAddr) {
        this.zkAddr = registerAddr;
    }


    /**
     * 1.连接注册中心
     * 2.清理无效服务
     * 3.注册服务
     * @throws Exception
     */
    public void connect() throws Exception {
        check();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        zkClient = CuratorFrameworkFactory
                        .builder()
                        .connectString(zkAddr)
                        .namespace(RpcConfiguration.NAME_SPACE)
                        .sessionTimeoutMs(ZK_CONNECT_TIME_OUT)
                        .retryPolicy(retryPolicy)
                        .build();

        zkClient.start();



        log.info("zk 正在注册服务");
        Set<String> serverSet = serviceList.getServiceName();
        for (String service : serverSet){
            zkClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ZK_SPILT+service.split(STR_SPILT)[0]+ZK_SPILT+SERVER_NAME,this.data.getBytes());
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



    private void check() {
        if (this.data == null){
            throw new NullPointerException("data");
        }
    }

    public void setData(String data){
        this.data = data;
    }

    public void close() {
        if (zkClient != null) {
            zkClient.close();
        }
    }

}
