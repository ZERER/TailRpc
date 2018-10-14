package com.tail.rpc.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weidong
 * @date Create in 15:03 2018/10/08
 **/
public class RpcNamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);

    private final String prefix;

    private final boolean daemoThread;

    public RpcNamedThreadFactory() {
        this("RPCThreadPool" ,false);
    }

    public RpcNamedThreadFactory(String prefix) {
        this(prefix, false);
    }

    public RpcNamedThreadFactory(String prefix, boolean daemo) {
        this.prefix = prefix + "-thread-";
        daemoThread = daemo;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = prefix + THREAD_NUMBER.getAndIncrement();
        Thread ret = new Thread(runnable, name);
        ret.setDaemon(daemoThread);
        return ret;
    }

}
