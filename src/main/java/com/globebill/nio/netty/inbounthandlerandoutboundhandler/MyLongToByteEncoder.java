package com.globebill.nio.netty.inbounthandlerandoutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kavin
 * @date 2023年03月21日 17:00
 */
@Slf4j
public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
        log.info("MyLongToByteEncoder.encode被调用");
        log.info("msg={}", msg);
        out.writeLong(msg);
    }
}
