package com.tail.rpc.exception;

/**
 * @author weidong
 * @date create in 21:23 2018/10/13
 **/
public class RpcServiceNotFindException extends RpcException {
    public RpcServiceNotFindException(String serverNode) {
        super(serverNode);
    }
}
