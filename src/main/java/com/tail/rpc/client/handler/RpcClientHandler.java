package com.tail.rpc.client.handler;

import com.tail.rpc.client.async.RpcFuture;
import com.tail.rpc.client.async.RpcFutureManager;
import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.model.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weidong
 * @date create in 14:39 2018/10/14
 **/
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static RpcFutureManager futureManager = RpcFutureManager.instance();

    private volatile Channel channel;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        String requestId = response.getId();
        RpcFuture future = futureManager.getRpcFuture(requestId);
        if (future != null) {
            futureManager.removeFuture(requestId);
            future.setResponse(response);
            ctx.close();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("warning... client caught exception", cause);
        ctx.close();
    }

    public void send(RpcRequest request) {
        channel.writeAndFlush(request);
    }
}
