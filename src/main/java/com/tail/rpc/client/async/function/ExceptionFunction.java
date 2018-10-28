package com.tail.rpc.client.async.function;

/**
 * @author weidong
 * @date create in 13:31 2018/10/28
 **/
@FunctionalInterface
public interface ExceptionFunction {
    /**
     * 调用异常执行下面方法
     * @param e 异常对象
     */
    void onError(Exception e);

}
