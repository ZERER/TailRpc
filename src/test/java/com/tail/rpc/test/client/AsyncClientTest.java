package com.tail.rpc.test.client;

import com.tail.rpc.client.RpcClient;
import com.tail.rpc.client.async.proxy.AsyncProxy;
import com.tail.rpc.test.service.HelloService;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author weidong
 * @date create in 13:01 2018/10/28
 **/
public class AsyncClientTest {

    private RpcClient client;

    @Before
    public void before(){
        client = new RpcClient("118.25.45.237:2181")
                .setRequestTimeOut(10, TimeUnit.SECONDS);
    }


    @Test
    public void testAsyncClient() throws ExecutionException, InterruptedException {
        AsyncProxy proxy = client.createAsyncProxy(HelloService.class);
        Future future = proxy.asyncInvoke("hello", 10).completedListener((result, e)->{
            if (e == null){
                System.out.println("执行完成");
            }
        });


        Object result = future.get();
        System.out.println("result = " + result);


        Thread.sleep(1000 * 60 * 60 * 24);

    }


    @Test
    public void testAsyncClientTwo() throws ExecutionException, InterruptedException {

        AsyncProxy proxy = client.createAsyncProxy(HelloService.class);
        Future future = proxy.asyncInvoke("hello", 10).completedListener((result, e)->{
            System.out.println("执行完成");

        });
        Thread.sleep(3 * 1000);

        Object result = future.get();
        System.out.println("result = " + result);
        client.close();
    }
}
