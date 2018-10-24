package com.tail.rpc.server;

import com.tail.rpc.server.handler.RpcServerHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import static com.tail.rpc.constant.RpcDefaultConfigurationValue.STR_SPILT;


/**
 * @author weidong
 * @date Create in 10:56 2018/10/11
 **/
@Slf4j
public class RpcServerBootstrap {

//    /**
//     * 提供外部访问的服务中心
//     */
//    private ServiceCenter serviceCenter = ServiceCenter.instance();

    /**
     * 是否运行
     */
    private boolean running = false;

    /**
     * 服务端运行配置文件
     */
    private RpcConfiguration configuration;

    /**
     * zookeeper操作
     */
    private ServerRegister zkClient;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    /**
     * 启动服务
     */
    public void start()  {
        if (this.running){
            log.warn("服务已经启动");
            return;
        }
        if (configuration.getServiceSize() == 0){
            throw new NullPointerException("service");
        }

        log.info("rpc服务正在启动...");
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new RpcServerHandlerInitializer(configuration));
        try {
            //netty start
            ChannelFuture future = bootstrap.bind(this.configuration.getServerAddr()).sync();

            //zookeeper register
            zkClient = new ServerRegister(this.configuration);
            zkClient.connect();
            log.info("rpc服务已经启动...");
            this.running = true;
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

    }

    public void stop(){
        log.info("关闭RPC服务");
        this.running = false;
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        zkClient.close();
    }


    public RpcServerBootstrap(){
        this.configuration = new RpcConfiguration();
    }

    public RpcServerBootstrap(String zkAddr){
        this();
        this.configuration.setZkAddr(zkAddr);
    }
    public RpcServerBootstrap(RpcConfiguration configuration){
        if (configuration == null){
            throw new NullPointerException("configuration");
        }
        this.configuration = configuration;
    }

    public RpcServerBootstrap register(String zkAddr){
        this.configuration.setZkAddr(zkAddr);
        return this;
    }

    public RpcServerBootstrap serverAddr(String serverAddr){
        String[] addrAndPort = serverAddr.split(STR_SPILT);
        if (addrAndPort.length <= 0){
            throw new RuntimeException("ip地址格式不正确");
        }
        return this.serverAddr(addrAndPort[0],Integer.valueOf(addrAndPort[1]));
    }

    public RpcServerBootstrap serverAddr(InetSocketAddress serverAddr){
        this.configuration.setServerAddr(serverAddr);
        return this;
    }

    public RpcServerBootstrap serverAddr(String hostname,Integer port){
        InetSocketAddress address = new InetSocketAddress(hostname,port);
        return this.serverAddr(address);
    }


    public RpcServerBootstrap serverName(String serverName){
        this.configuration.setServerName(serverName);
        return this;
    }


    public RpcConfiguration getConfiguration(){
        return this.configuration;
    }

    public RpcServerBootstrap addService(Object service) {
        this.configuration.addService(service);
        return this;
    }

    public RpcServerBootstrap addService(String serviceName,Object service) {
        this.configuration.addService(serviceName,service);
        return this;
    }

    public RpcServerBootstrap addService(Class<?> rpcInterface,Object service) {
        this.configuration.addService(rpcInterface.getSimpleName(),service);
        return this;
    }


}
