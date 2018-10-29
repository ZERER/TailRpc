package com.tail.rpc.test.client;

import com.tail.rpc.client.RpcClient;
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
 * @date create in 21:31 2018/10/29
 **/
public class AsynConcurrentTest {

    final RpcClient client  = new RpcClient("118.25.45.237:2181");
    ThreadPoolExecutor pool = RpcThreadPool.getExecutor(10, -1, "ConcurrentThreadPool");
    @Test
    public void testConcurrent() throws ExecutionException, InterruptedException {





        List<Future<?>> futures = new LinkedList<>();
        long start = System.currentTimeMillis();
        for (int i = 0 ; i < 100; i ++){
            final int x = i;
            Future<?> submit = pool.submit(() -> {
                HelloService helloService = client.create(HelloService.class);
                String hello = helloService.hello(x);
                System.out.println("result="+hello);

            });
            futures.add(submit);
        }


        for (Future<?> f : futures){
            f.get();
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时:"+(end - start));
    }

}
