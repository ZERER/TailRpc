package com.tail.rpc.client.async.function;

/**
 * @author weidong
 * @date create in 13:28 2018/10/28
 **/
@FunctionalInterface
public interface CompletedFunction {
    /**
     * 异步执行完成后调用
     * @param result 执行结果
     * @param e 抛出异常 ，成功执行e为null
     */
    void complete(Object result, Exception e);
}
