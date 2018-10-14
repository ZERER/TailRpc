package com.tail.rpc.client.handler;

import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author weidong
 * @date create in 14:39 2018/10/14
 **/
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {

    }
}
