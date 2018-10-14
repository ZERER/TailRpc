package com.tail.rpc.client.balance;


import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author weidong
 * @date create in 14:49 2018/10/13
 **/
public interface RpcBalance {

    /**
     * 负载均衡，返回负载后的提供者信息
     * @param server 服务名
     * @return
     */
    InetSocketAddress select(String server);

}
