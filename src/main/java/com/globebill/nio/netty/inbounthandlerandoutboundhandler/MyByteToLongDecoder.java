package com.globebill.nio.netty.inbounthandlerandoutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Kavin
 * @date 2023年03月21日 17:00
 */
@Slf4j
public class MyByteToLongDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("MyByteToLongDecoder.decode被调用");
        // 因为 long 8个字节，需要判断有8个字节，才能读取一个 long
        if (in.readableBytes() >= 8) {
            out.add(in.readLong());
        }
    }
}
