package com.tail.rpc.client;

import com.tail.rpc.client.balance.DefaultBalance;
import com.tail.rpc.client.balance.RpcBalance;
import com.tail.rpc.client.handler.RpcClientHandler;
import com.tail.rpc.client.handler.RpcClientInitializer;
import com.tail.rpc.exception.RpcConnectException;
import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.model.RpcResponse;
import com.tail.rpc.thread.RpcThreadPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author weidong
 * @date create in 13:50 2018/10/13
 **/
@Slf4j
public class RpcConnectManager {

    private final RpcBalance balance;

    private final static ThreadPoolExecutor EXECUTOR = RpcThreadPool.getClientDefaultExecutor();

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(RpcThreadPool.THREAD_NUM);

    private RpcClientHandler handler = new RpcClientHandler();

    public RpcConnectManager(String zkAddr) {
        balance = new DefaultBalance(new ClientRegister(zkAddr));
    }



    /**
     * 同步请求服务
     *
     * @param request rpc请求体
     * @return rpc返回体
     */
    public RpcResponse handle(RpcRequest request) {
        //获取服务地址
        SocketAddress serverAddr = balance.select(request.getServiceClass().getName());
        RpcResponse response;
        try {
            response = remoteRequest(serverAddr, request);
            return response;
        } catch (Exception e) {
            log.error("请求失败");
        }
        return null;
    }



    private RpcResponse remoteRequest(SocketAddress serverAddr, RpcRequest request) {
        EXECUTOR.submit(() -> {
            Bootstrap b = new Bootstrap();
            b.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new RpcClientInitializer());

            ChannelFuture channelFuture = b.connect(serverAddr);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        log.debug("Successfully connect to remote server. remote peer = " + serverAddr);
                        //RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
                        //addHandler(handler);
                    }
                }
            });
        });
        return null;
    }

    public void close(){
        if (eventLoopGroup != null){
            eventLoopGroup.shutdownGracefully();
        }
    }
}




