package com.tail.rpc.test.client;

import com.tail.rpc.client.RpcClient;
import com.tail.rpc.test.service.HelloService;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author weidong
 * @date create in 20:14 2018/10/14
 **/
public class SyncClientTest {

    @Test
    public void testRequest() throws InterruptedException {
        RpcClient client = new RpcClient("118.25.45.237:2181")
                .setRequestTimeOut(10, TimeUnit.SECONDS);
        HelloService service = client.create(HelloService.class);
        String result = service.hello(1000);
        System.out.println("testRequest result = " + result);
        Thread.sleep(1000 * 60* 60 * 24);

    }

    @Test
    public void testRequestByServer() throws InterruptedException {
        RpcClient client = new RpcClient("118.25.45.237:2181")
                .setRequestTimeOut(10, TimeUnit.SECONDS);
        HelloService service = client.create(HelloService.class,"test");
        String result = service.hello(1000);
        System.out.println("testRequestByServer result = " + result);


        Thread.sleep(1000 * 60* 60 * 24);

    }

}
