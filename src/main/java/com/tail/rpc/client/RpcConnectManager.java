package com.tail.rpc.client;

import com.tail.rpc.client.balance.DefaultBalance;
import com.tail.rpc.client.balance.RpcBalance;
import com.tail.rpc.client.handler.RpcClientHandler;
import com.tail.rpc.client.handler.RpcClientInitializer;
import com.tail.rpc.client.service.LocalServer;
import com.tail.rpc.client.service.ServiceBean;
import com.tail.rpc.exception.RpcConnectException;
import com.tail.rpc.exception.RpcServiceNotFindException;
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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author weidong
 * @date create in 13:50 2018/10/13
 **/
@Slf4j
public class RpcConnectManager {

    private RpcBalance balance = new DefaultBalance();

    private final static ThreadPoolExecutor EXECUTOR = RpcThreadPool.getClientDefaultExecutor();

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(RpcThreadPool.THREAD_NUM);

    private RpcClientHandler handler = new RpcClientHandler();

    private  ClientRegister zkClient;

    private LocalServer localServer = LocalServer.instance();

    public RpcConnectManager(String zkAddr) {
        zkClient = new ClientRegister(zkAddr);
    }

    public RpcConnectManager(String zkAddr,RpcBalance balance) {
        this(zkAddr);
        this.balance = balance;
    }

    /**
     * 同步请求服务
     *
     * @param request rpc请求体
     * @return rpc返回体
     */
    public RpcResponse handle(RpcRequest request) {
        //获取服务地址
        SocketAddress serverAddr = getServer(request.getServiceClass().getName());
        RpcResponse response;
        try {
            response = remoteRequest(serverAddr, request);
            return response;
        } catch (Exception e) {
            log.error("请求失败");
        }
        return null;
    }

    private SocketAddress getServer(String server) {
        if(localServer.size() > 0){
            //本地查找
            List<ServiceBean> serverNodes = localServer.getService(server);
            if (serverNodes.size() > 0){
                InetSocketAddress serverAddr = balance.select(serverNodes);
                if (serverAddr != null){
                    return serverAddr;
                }
            }
        }
        //去注册中心查找服务
        List<InetSocketAddress> serverNodes = zkClient.getServer(server);
        if (serverNodes.size() > 0){
            InetSocketAddress serverAddr = balance.select(localServer.putServer(server,serverNodes));
            if (serverAddr != null){
                return serverAddr;
            }
        }

        throw new RpcServiceNotFindException(server);
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




