package com.tail.rpc.client;

import com.tail.rpc.service.HelloService;

import java.util.concurrent.TimeUnit;

/**
 * @author weidong
 * @date create in 20:14 2018/10/14
 **/
public class HelloClient {
    public static void main(String[] args) {
        RpcClient client = new RpcClient("118.25.45.237:2181")
                .setRequestTimeOut(10, TimeUnit.SECONDS);
        HelloService service = client.create(HelloService.class);
        String result = service.hello(10);
        System.out.println("result = " + result);
        client.close();
    }
}
