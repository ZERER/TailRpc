package com.tail.rpc.client;

import com.tail.rpc.service.HelloService;

/**
 * @author weidong
 * @date create in 20:14 2018/10/14
 **/
public class HelloTest {
    public static void main(String[] args) {
        RpcClient client = new RpcClient("4855");
        HelloService service  = client.create(HelloService.class);
        service.hello(10);

    }
}
