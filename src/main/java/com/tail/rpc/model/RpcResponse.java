package com.tail.rpc.model;

import lombok.Data;

/**
 * @author weidong
 * @date Create in 14:54 2018/10/11
 **/
@Data
public class RpcResponse {
    private String requestId;
    private boolean status;
    private String error;
    private Object result;
}
