package com.tail.rpc.client.async;

import com.tail.rpc.exception.RpcException;
import com.tail.rpc.exception.RpcTimeOutException;
import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author weidong
 * @date create in 20:18 2018/10/16
 **/
@Slf4j
public class RpcFuture implements Future<Object> {

    /**
     * 请求状态
     * 1.请求中
     * 2.请求结束
     * 3.请求取消
     */
    private volatile RpcRequestStatus status;
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

    private List<BiConsumer<Object,Exception>> completeCallBacks;

    private Consumer<Exception> onErrorCallBack;

    public RpcFuture(RpcRequest request) {
        status = RpcRequestStatus.RUNNING;
        this.request = request;
        this.sync = new Sync();
    }

    @Override
    public boolean cancel(boolean isCancel) {
        if (status == RpcRequestStatus.CANCEL) {
            throw new RpcException("该请求不能取消多次");
        }
        status = RpcRequestStatus.CANCEL;
        return true;

    }

    @Override
    public boolean isCancelled() {
        return status.equals(RpcRequestStatus.CANCEL);
    }

    @Override
    public boolean isDone() {
        return status.equals(RpcRequestStatus.FINISHED);
    }

    @Override
    public Object get() throws InterruptedException {
        return get(request.getTimeOut(), request.getUnit());
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        if (status.equals(RpcRequestStatus.CANCEL)) {
            throw new InterruptedException("该请求已经取消");
        }

        if (status.equals(RpcRequestStatus.RUNNING) || sync.tryAcquireNanos(1, unit.toNanos(timeout))) {
            status = RpcRequestStatus.FINISHED;
            if (response.isSuccess()) {
                return this.response.getResult();
            }
            throw new InterruptedException(response.getError().getMessage());
        }

        //超时异常
        throw new RpcTimeOutException("requestId = " + request.getId() + "request TimeOut, timeout : " + request.getTimeOut() + ", TimeUnit : " + request.getUnit().toString());

    }

    public void setResponse(RpcResponse response) {
        log.info("requestId = {} finish", response.getId());
        this.response = response;
        sync.release(1);
        callCompelete();
        if (!response.isSuccess()){
            callOnError();
        }
    }

    private void callOnError() {
        if (onErrorCallBack==null){
            return;
        }
        onErrorCallBack.accept(response.getError());
    }

    private void callCompelete() {
        if (completeCallBacks == null){
            return;
        }
        final List<BiConsumer<Object,Exception>> callBacks = new LinkedList<>(completeCallBacks);
        callBacks.forEach(c -> c.accept(response.getResult(),response.getError()));
    }


    public RpcFuture compeleted(BiConsumer<Object,Exception> complete) {
        if (completeCallBacks == null){
            completeCallBacks = new LinkedList<>();
        }
        completeCallBacks.add(complete);
        return this;
    }

    public RpcFuture onError(Consumer<Exception> e) {
            onErrorCallBack = e;
        return this;
    }
}
