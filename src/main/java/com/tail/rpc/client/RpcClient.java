package com.tail.rpc.client;


import java.lang.reflect.Proxy;

import static com.tail.rpc.constant.RpcConfiguration.ZK_ADDR;

/**
 * @author weidong
 * @date create in 12:29 2018/10/13
 **/
public class RpcClient {

    private String zkAddr;
//
//    private String serverNode ;
//
//    public RpcClient(String zkAddr,String serverNode){
//        this.zkAddr = zkAddr;
//        this.serverNode = serverNode;
//    }
//
//    public RpcClient(){
//        this(ZK_ADDR);
//    }

    public RpcClient(){
        this(ZK_ADDR);
    }

    public RpcClient(String zkAddr) {
        this.zkAddr = zkAddr;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> inter){
        return (T) Proxy.newProxyInstance(
                inter.getClassLoader(),
                new Class<?>[]{inter},
                //new RpcProxyInvoker<>(inter,zkAddr,serverNode)
                new RpcProxyInvoker<>(inter,zkAddr)
        );
    }


}
