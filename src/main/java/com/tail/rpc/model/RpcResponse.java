package com.tail.rpc.model;

import lombok.Data;

/**
 * @author weidong
 * @date Create in 14:54 2018/10/11
 **/
@Data
public class RpcResponse {
    /**
     * 请求id,与RPCRequest的id对应
     */
    private String id;
    /**
     * 返回状态
     */
    private boolean success;
    /**
     * 错误信息
     */
    private Exception error;
    /**
     * 返回结果
     */
    private Object result;
}
