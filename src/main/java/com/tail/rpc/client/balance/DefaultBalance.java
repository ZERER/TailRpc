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


    private static AtomicInteger index = new AtomicInteger(1);

    @Override
    public InetSocketAddress select(List<ServiceBean> server) {
        return server.get(index.getAndIncrement() % server.size()).getInetAddress();
    }





}
