package com.tail.rpc.exception;

/**
 * @author weidong
 * @date Create in 15:10 2018/10/23
 **/
public class RpcTimeOutException extends RpcException {
    public RpcTimeOutException(){}

    public RpcTimeOutException(String msg){
        super(msg);
    }
}
