package com.tail.rpc.client.async;

/**
 * @author weidong
 * @date create in 16:52 2018/10/27
 **/
public enum  RpcRequestStatus {
    /**
     * 运行状态
     */
    RUNNING(1),
    /**
     *结束状态
     */
    FINISHED(2),
    /**
     *取消状态
     */
    CANCEL(3);

    private Integer status;


    RpcRequestStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
