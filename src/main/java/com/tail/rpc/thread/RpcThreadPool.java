package com.tail.rpc.thread;

import java.util.concurrent.*;

/**
 * @author weidong
 * @date Create in 15:21 2018/10/08
 * 独立出线程池主要是为了应对复杂耗I/O操作的业务，不阻塞netty的handler线程而引入
 **/
public class RpcThreadPool {

    public final static int THREAD_NUM;

    static {
        THREAD_NUM = Runtime.getRuntime().availableProcessors();
    }

    /**
     *
     * @param threads 线程池数量
     * @param queues 队列容量
     * @return Executor
     */
    public static ThreadPoolExecutor getExecutor(int threads, int queues,String rpcThreadName) {
        return new ThreadPoolExecutor(threads, threads,
                0, TimeUnit.MILLISECONDS,
                queues < 0 ? new LinkedBlockingQueue<>() : new LinkedBlockingQueue<>(queues),
                new RpcNamedThreadFactory(rpcThreadName, true), new RpcRejectedPolicy(rpcThreadName));
    }
    /**
     * @return Executor 默认线程池
     */
    public static ThreadPoolExecutor getWatchDefaultExecutor() {
        return getExecutor(THREAD_NUM / 2,-1,"RpcWatchThreadPool");
    }

    /**
     * @return Executor 默认线程池
     */
    public static ThreadPoolExecutor getServerDefaultExecutor() {
        return getExecutor(THREAD_NUM,-1,"RpcServerThreadPool");
    }
    /**
     * @return Executor 默认线程池
     */
    public static ThreadPoolExecutor getClientDefaultExecutor() {
        return getExecutor(THREAD_NUM,-1,"RpcClientThreadPool");
    }
}
