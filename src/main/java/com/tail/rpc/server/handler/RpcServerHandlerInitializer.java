package com.tail.rpc.server.handler;

import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.model.RpcResponse;
import com.tail.rpc.procotol.RpcDecoder;
import com.tail.rpc.procotol.RpcEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author weidong
 * @date create in 19:26 2018/10/14
 **/
public class RpcServerHandlerInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        pipeline.addLast(new RpcDecoder(RpcRequest.class));
        pipeline.addLast(new RpcEncoder(RpcResponse.class));
        pipeline.addLast(new RpcServerHandler());
    }
}
