package com.tail.rpc.exception;

/**
 * @author weidong
 * @date Create in 10:25 2018/10/12
 **/
public class RpcException extends RuntimeException {


    public RpcException(){}

    public RpcException(String msg){
        super(msg);
    }

}
