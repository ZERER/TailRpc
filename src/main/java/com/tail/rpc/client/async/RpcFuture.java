package com.tail.rpc.client.async;

import com.tail.rpc.model.RpcRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author weidong
 * @date create in 20:18 2018/10/16
 **/
public class RpcFuture implements Future<Object> {

    private RpcRequest request;
    private Object result;

    public RpcFuture(RpcRequest request) {
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    public void setResult(Object result){
        this.result = result;
    }

}
