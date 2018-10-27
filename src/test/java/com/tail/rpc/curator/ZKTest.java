package com.tail.rpc.curator;

import com.tail.rpc.constant.RpcDefaultConfigurationValue;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

import static com.tail.rpc.constant.RpcDefaultConfigurationValue.DEFAULT_ZK_ADDRESS;
import static com.tail.rpc.constant.RpcDefaultConfigurationValue.ZK_CONNECT_TIME_OUT;
import static java.lang.Thread.sleep;

/**
 * @author weidong
 * @date create in 10:55 2018/10/14
 **/
public class ZKTest {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework zkClient = CuratorFrameworkFactory
                .builder()
                .connectString(DEFAULT_ZK_ADDRESS)
                .namespace(RpcDefaultConfigurationValue.NAME_SPACE)
                .sessionTimeoutMs(ZK_CONNECT_TIME_OUT)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        List<String> nodes = zkClient.getChildren().forPath("/");
        for (String node : nodes){
            System.out.println(node);
            List<String> server = zkClient.getChildren().forPath("/"+node);
            System.out.println(server);



        }


        sleep(100000000);

    }
}
