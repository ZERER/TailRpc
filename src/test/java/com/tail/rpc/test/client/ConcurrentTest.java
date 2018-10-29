package com.tail.rpc.test.client;

import com.tail.rpc.client.RpcClient;
import com.tail.rpc.client.async.RpcFuture;
import com.tail.rpc.client.async.proxy.AsyncProxy;
import com.tail.rpc.test.service.HelloService;
import com.tail.rpc.thread.RpcThreadPool;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author weidong
 * @date create in 20:49 2018/10/29
 **/
public class ConcurrentTest {

    final RpcClient client = new RpcClient("192.168.88.12:2181");

    ThreadPoolExecutor pool = RpcThreadPool.getExecutor(10, -1, "ConcurrentThreadPool");

    @Test
    public void testConcurrent() throws ExecutionException, InterruptedException {


        long start = System.currentTimeMillis();
        List<Future> futures = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            final int x = i;
            Future<?> future = pool.submit(() -> {
                AsyncProxy proxy = client.createAsyncProxy(HelloService.class);
                RpcFuture rpcFuture = proxy.asyncInvoke("hello", x);
                try {
                    System.out.println("result=" + rpcFuture.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
            futures.add(future);
        }

        futures.forEach(f->{
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("耗时:" + (end - start));
    }

}
