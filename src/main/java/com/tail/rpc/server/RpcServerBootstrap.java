package com.tail.rpc.server;

import com.tail.rpc.constant.RpcConfiguration;
import com.tail.rpc.exception.RpcException;
import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.model.RpcResponse;
import com.tail.rpc.procotol.RpcDecoder;
import com.tail.rpc.procotol.RpcEncoder;
import com.tail.rpc.server.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;


/**
 * @author weidong
 * @date Create in 10:56 2018/10/11
 **/
@Slf4j
public class RpcServerBootstrap {
    /**
     * 服务提供者ip:端口号
     * example 127.0.0.1:8080
     */
    private String serverAddr;

    /**
     * 注册中心地址,默认为RpcConfiguration.ZK_ADDR
     * @see RpcConfiguration
     */
    private String registerAddr = RpcConfiguration.ZK_ADDR;

    /**
     * 提供外部访问的服务中心
     */
    private ServiceCenter serviceCenter = ServiceCenter.instance();

    /**
     * 是否运行
     */
    private boolean running = false;

    /**
     * zookeeper操作
     */
    private ServerRegister zkClient;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    private final static String PORT_IP_SPILT = ":";

    public RpcServerBootstrap register(String registerAddr){
        this.registerAddr = registerAddr;
        return this;
    }

    public RpcServerBootstrap server(String serverAddr){
        this.serverAddr = serverAddr;
        return this;
    }

    public RpcServerBootstrap addService(Object service){
        serviceCenter.addService(service);
        return this;
    }

    public RpcServerBootstrap addService(Class<?> interfaceClass,Object service){
        serviceCenter.addService(interfaceClass,service);
        return this;
    }

    /**
     * 启动服务
     */
    public void start()  {
        if (this.running){
            log.warn("服务已经启动");
            return;
        }
        check();
        log.info("rpc服务正在启动...");
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                        pipeline.addLast(new RpcDecoder(RpcRequest.class));
                        pipeline.addLast(new RpcEncoder(RpcResponse.class));
                        pipeline.addLast(new RpcServerHandler());
                    }
            });

        try {
            String[] serverAndPort = serverAddr.split(PORT_IP_SPILT);
            String host = serverAndPort[0];
            Integer port = new Integer(serverAndPort[1]);

            //netty start
            ChannelFuture future = bootstrap.bind(host, port).sync();

            //zookeeper register
            zkClient = new ServerRegister(this.registerAddr);
            zkClient.setData(serverAddr);
            zkClient.connect();
            log.info("rpc服务已经启动...");
            future.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.info("rpc服务结束");
                }
            }).sync();
        } catch (Exception e) {
            log.error("服务启动失败,失败原因:{}",e.getMessage());
        } finally {
            stop();
        }
        this.running = true;
    }

    public void stop(){
        log.info("关闭RPC服务");
        this.running = false;
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        zkClient.close();
    }

    private void check() {
        if(serverAddr == null || registerAddr == null || serviceCenter.size() == 0){
            log.error("serverAddr="+serverAddr+",registerAddr="+registerAddr+",serverCenter数量为"+serviceCenter.size());
            throw new RpcException("不完整的参数");
        }
    }


}
