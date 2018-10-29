package com.tail.rpc.test.server;

import com.tail.rpc.server.RpcServerBootstrap;
import com.tail.rpc.test.service.HelloServiceImpl;
import org.junit.Test;

/**
 * @desc:
 * @author: weidong
 * @date: create in 10:08 2018/10/13
 **/
public class ServerBootstrapTest {

    @Test
    public void testStartHelloService() {
        RpcServerBootstrap bootstrap = new RpcServerBootstrap();
        bootstrap.register("192.168.88.12:2181")
                .addService(new HelloServiceImpl())
                .serverAddr("127.0.0.1:8080")
                .start();
    }

    @Test
    public void testStartHelloServiceTwo() {
        RpcServerBootstrap bootstrap = new RpcServerBootstrap();
        bootstrap.register("192.168.88.12:2181")
                .addService(new HelloServiceImpl())
                .serverAddr("127.0.0.1:8081")
                .start();
    }

    @Test
    public void testStartHelloServiceByServerName() {
        RpcServerBootstrap bootstrap = new RpcServerBootstrap();
        bootstrap.register("192.168.88.12:2181")
                .addService(new HelloServiceImpl())
                .serverAddr("127.0.0.1:8080")
                .serverName("test")
                .start();
    }
}
