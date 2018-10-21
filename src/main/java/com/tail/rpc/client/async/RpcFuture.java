package com.tail.rpc.client.async;

import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author weidong
 * @date create in 20:18 2018/10/16
 **/
@Slf4j
public class RpcFuture implements Future<Object> {
    /**
     * 请求体
     */
    private RpcRequest request;
    /**
     * 响应体
     */
    private RpcResponse response;
    /**
     * 同步组件(自定义独占式同步队列)
     */
    private final Sync sync;

    public RpcFuture(RpcRequest request) {
        this.request = request;
        this.sync = new Sync();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("cancel");
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException("isCancelled");
    }

    @Override
    public boolean isDone() {
        return sync.isFinish();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        try {
            return get(request.getTimeOut(),request.getUnit());
        } catch (TimeoutException e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if(sync.tryAcquireNanos(1, unit.toNanos(timeout))){
            if (response.isSuccess()){
                return response.getResult();
            }else {
                throw new ExecutionException(response.getError());
            }
        }
        log.error("Id :{} , ClassName : {} , MethodName : {} , ParameterType : {} , Parameter : {}" +
                        " RequestTimeOut . TimeOut : {} TimeUnit : {} ",
                request.getId(), request.getService(),
                request.getParameterTypes(), request.getParameters(),
                timeout , unit.toString());
        throw new TimeoutException("execute time out");
    }

    public void setResponse(RpcResponse response){
        log.info("requestId = {} finish",response.getId());
        this.response = response;
        sync.release(1);
    }

}
