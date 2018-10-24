package com.tail.rpc.server.handler;

import com.tail.rpc.model.RpcRequest;
import com.tail.rpc.model.RpcResponse;
import com.tail.rpc.server.RpcConfiguration;
import com.tail.rpc.thread.RpcThreadPool;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author weidong
 * @date Create in 11:58 2018/10/12
 **/
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final RpcConfiguration configuration;


    public RpcServerHandler(RpcConfiguration configuration){
        this.configuration = configuration;
    }

    private ThreadPoolExecutor pool = RpcThreadPool.getServerDefaultExecutor();

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final RpcRequest request)  {
        pool.submit(()->{
            log.debug("接受RPC请求 请求id:{}", request.getId());
            //由线程池处理
            RpcResponse response = requestHandler(request);
            ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture ->
                    log.debug("请求处理完成 请求id:{}" , request.getId()));
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server caught exception", cause);
        ctx.close();
    }
    /**
     * 处理请求
     * @param request rpcRequest
     * @return 处理结果
     */
    private RpcResponse requestHandler(RpcRequest request){

        String id = request.getId();
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Object serviceBean = configuration.getServiceMap().get(className);
        log.info("调用:{}的方法{}:",serviceBean.getClass().getName(),methodName);
        log.info("方法参数类型");
        for (Class<?> type  : parameterTypes ){
            log.info(type.getName()+" ");
        }
        log.info("调用传值:");
        for (Object param  : parameters ){
            log.info(param+" ");
        }
        RpcResponse response = new RpcResponse();
        response.setId(id);
        try {


            Method invoker = serviceBean.getClass().getMethod(methodName,parameterTypes);
            invoker.setAccessible(true);
            Object result = invoker.invoke(serviceBean,parameters);
            log.info("执行结果:{}",request);
            response.setResult(result);
            response.setSuccess(true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("处理异常，原因",e.getMessage());
            response.setError(e);
            response.setSuccess(false);
        }
        return response;
    }

}
