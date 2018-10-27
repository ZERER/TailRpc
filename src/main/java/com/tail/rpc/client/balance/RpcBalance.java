package com.tail.rpc.client.balance;


import com.tail.rpc.client.service.ServiceBean;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author weidong
 * @date create in 14:49 2018/10/13
 **/
public interface RpcBalance {

    /**
     * 负载均衡，返回负载后的提供者信息
     * @param serverBean 可用的服务
     * @return 请求服务的网络地址
     */
    InetSocketAddress select(List<ServiceBean> serverBean);

}
