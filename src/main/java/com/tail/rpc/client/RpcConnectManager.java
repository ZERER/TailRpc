package com.tail.rpc.client;

import com.tail.rpc.client.async.RpcFuture;
import com.tail.rpc.client.async.RpcFutureManager;
import com.tail.rpc.client.balance.DefaultBalance;
import com.tail.rpc.client.balance.RpcBalance;
import com.tail.rpc.client.handler.RpcClientHandler;
import com.tail.rpc.client.handler.RpcClientInitializer;
import com.tail.rpc.client.service.LocalServer;
import com.tail.rpc.client.service.ServiceBean;
import com.tail.rpc.exception.RpcServiceNotFindException;
import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.thread.RpcThreadPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author weidong
 * @date create in 13:50 2018/10/13
 **/
@Slf4j
public class RpcConnectManager {

    private RpcBalance balance = new DefaultBalance();

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(RpcThreadPool.THREAD_NUM);

    private  ClientRegister zkClient;

    private LocalServer localServer = LocalServer.instance();

    private RpcFutureManager futureManager =RpcFutureManager.instance();

    private String serverName;

    public RpcConnectManager(ClientRegister zkClient) {
        this.zkClient = zkClient;
        zkClient.connect();
    }

    public RpcConnectManager(ClientRegister zkClient,RpcBalance balance) {
        this(zkClient);
        this.balance = balance;
    }

    /**
     * 同步请求服务
     *
     * @param request rpc请求体
     * @return 返回结果
     */
    public Object handle(RpcRequest request) throws Exception {
        return remoteRequest(getServer(request.getClassName()), request).get();
    }

    /**
     * 查找服务地址，先本地后远程
     * @param server 请求服务名字
     * @return 请求地址
     */
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
        List<ServiceBean> serverNodes = zkClient.getServer(server);
        if (serverNodes.size() > 0){
            InetSocketAddress serverAddr = balance.select(serverNodes);
            if (serverAddr != null){
                return serverAddr;
            }
        }

        throw new RpcServiceNotFindException(server);
    }

    /**
     * 打开netty连接，远程调用RPC服务
     * @param serverAddr 请求地址
     * @param request 请求体
     * @return future
     */
    private RpcFuture remoteRequest(SocketAddress serverAddr, RpcRequest request) {
        RpcFuture future = new RpcFuture(request);
        futureManager.putRpcFuture(request.getId(),future);

        RpcClient.submit(() -> {

            CountDownLatch countDownLatch = new CountDownLatch(1);
            try {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new RpcClientInitializer());

                ChannelFuture channelFuture = b.connect(serverAddr);
                channelFuture.addListener(cf-> {
                    if (cf.isSuccess()) {
                        log.info("Successfully connect to remote server. remote peer = " + serverAddr);
                        countDownLatch.countDown();
                    }
                });
                //等待连接完成
                countDownLatch.await();
                RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
                handler.send(request);
            } catch (Exception e) {
                log.error("错误:{}",e.getMessage());
            }

        });
        return future;
    }

    public void close(){
        if (eventLoopGroup != null){
            eventLoopGroup.shutdownGracefully();
        }

        if (zkClient != null){
            zkClient.close();
        }
    }

}




