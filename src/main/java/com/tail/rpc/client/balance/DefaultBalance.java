package com.tail.rpc.client.balance;

import com.tail.rpc.client.service.ServiceBean;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weidong
 * @date create in 14:51 2018/10/13
 **/
public class DefaultBalance implements RpcBalance{


    /**
     * 按轮询的方式进行负载均衡
     */
    private static AtomicInteger index = new AtomicInteger(0);

    @Override
    public InetSocketAddress select(List<ServiceBean> server) {
        ServiceBean service = server.get(index.getAndIncrement() % server.size());
        service.getAndIncrement();
        return service.getInetAddress();
    }





}
