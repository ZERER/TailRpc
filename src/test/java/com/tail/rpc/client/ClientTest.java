package com.tail.rpc.client;

import com.tail.rpc.service.HelloService;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author weidong
 * @date create in 20:14 2018/10/14
 **/
public class ClientTest {

    @Test
    public void testRequest() throws InterruptedException {
        RpcClient client = new RpcClient("118.25.45.237:2181")
                .setRequestTimeOut(10, TimeUnit.SECONDS);
        HelloService service = client.create(HelloService.class);
        System.out.println("发起请求");
        String result = service.hello(1000);
        System.out.println("result = " + result);


        Thread.sleep(60* 60 * 24);

    }

}
