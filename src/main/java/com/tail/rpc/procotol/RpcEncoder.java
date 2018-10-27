package com.tail.rpc.procotol;

import com.tail.rpc.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * protostuff编码器
 * @author weidong
 * @date Create in 11:55 2018/10/12
 **/
public class RpcEncoder extends MessageToByteEncoder{

    /**
     * 编码的类
     */
    private Class<?> encoderClass;

    public RpcEncoder(Class<?> rpcResponseClass) {
        this.encoderClass = rpcResponseClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (encoderClass.isInstance(in)) {
            byte[] data = ProtostuffUtils.serializer(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }

    }
}
