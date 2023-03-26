package com.globebill.nio.netty.inbounthandlerandoutboundhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kavin
 * @date 2023/3/21 21:56
 */
@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<Long> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        log.info("从服务端 {} 读取到: {}", ctx.channel().remoteAddress(), msg);
        ctx.writeAndFlush("");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("myClientHandler 发送数据");
        ctx.writeAndFlush(123456L);
        /*
         * if (acceptOutboundMessage(msg)) { // 判断当前msg 是不是应该处理的类型
         *     @SuppressWarnings("unchecked")
         *     I cast = (I) msg;
         *     buf = allocateBuffer(ctx, cast, preferDirect);
         *     try {
         *         encode(ctx, cast, buf);
         *     } finally {
         *         ReferenceCountUtil.release(cast);
         *     }
         *
         *     if (buf.isReadable()) {
         *         ctx.write(buf, promise);
         *     } else {
         *         buf.release();
         *         ctx.write(Unpooled.EMPTY_BUFFER, promise);
         *     }
         *     buf = null;
         * } else {
         *     ctx.write(msg, promise);
         * }
         */
        // 因此我们编写 Encoder 时要注意传入的数据类型和处理的数据类型一致
        // ctx.writeAndFlush(Unpooled.copiedBuffer("abcdabcdefghijkl".getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
