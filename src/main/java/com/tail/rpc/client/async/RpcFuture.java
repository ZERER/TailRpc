package com.tail.rpc.client.async;

import com.tail.rpc.exception.RpcTimeOutException;
import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author weidong
 * @date create in 20:18 2018/10/16
 **/
@Slf4j
public class RpcFuture implements Future<RpcResponse> {
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
    public RpcResponse get() throws InterruptedException {
        return get(request.getTimeOut(),request.getUnit());
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException {
        if(sync.tryAcquireNanos(1, unit.toNanos(timeout))){
            return response;
        }
        log.error("Id :{} , ClassName : {} , MethodName : {} , ParameterType : {} , Parameter : {}" +
                        " RequestTimeOut . TimeOut : {} TimeUnit : {} ",
                request.getId(), request.getClassName(),request.getMethodName(),
                request.getParameterTypes(), request.getParameters(),
                timeout , unit.toString());

        response =new RpcResponse();
        response.setSuccess(false);
        response.setError(new RpcTimeOutException("requestId = "+request.getId() + "request TimeOut, timeout : "+request.getTimeOut() + ", TimeUnit : " + request.getUnit().toString()));
        return response;
    }

    public void setResponse(RpcResponse response){
        log.info("requestId = {} finish",response.getId());
        this.response = response;
        sync.release(1);
    }

}
