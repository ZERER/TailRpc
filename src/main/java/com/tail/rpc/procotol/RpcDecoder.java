package com.tail.rpc.procotol;

import com.tail.rpc.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author weidong
 * @date Create in 11:55 2018/10/12
 **/
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> decoderClass;

    private static final int INTEGER_SIZE = 4;

    private static final int ZERO_SIZE = 0;

    public RpcDecoder(Class<?> decoderClass) {
        this.decoderClass = decoderClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < INTEGER_SIZE) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < ZERO_SIZE) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = ProtostuffUtils.deserializer(data, decoderClass);
        log.info("对{}进行解码",decoderClass.getName());
        out.add(obj);
    }
}