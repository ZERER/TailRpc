package com.tail.rpc.client.service;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weidong
 * @date create in 20:42 2018/10/15
 **/
public class ServiceBean {
    private InetSocketAddress inetAddress;

    private AtomicInteger num = new AtomicInteger(0);

    public ServiceBean(String address,Integer port){
        inetAddress = new InetSocketAddress(address,port);
    }

    public ServiceBean(InetSocketAddress inetAddress){
        this.inetAddress = inetAddress;
    }

    public InetSocketAddress getInetAddress() {
        return inetAddress;
    }

    public int getNum() {
        return num.get();
    }

    public int getAndIncrement(){
        return num.getAndIncrement();
    }
}
