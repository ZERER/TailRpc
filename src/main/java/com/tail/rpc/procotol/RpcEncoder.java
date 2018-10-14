package com.tail.rpc.procotol;

import com.tail.rpc.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weidong
 * @date Create in 11:55 2018/10/12
 **/
@Slf4j
public class RpcEncoder extends MessageToByteEncoder{

    private Class<?> encoderClass;

    public RpcEncoder(Class<?> rpcResponseClass) {
        this.encoderClass = rpcResponseClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (encoderClass.isInstance(in)) {
            log.info("对{}进行编码",encoderClass);
            byte[] data = ProtostuffUtils.serializer(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }

    }
}
