package com.globebill.nio.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 这里 TextWebSocketFrame 类型，表示一个文本帧(frame)
 *
 * @author Kavin
 * @date 2023/3/19 22:18
 */
@Slf4j
public class MyTextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("服务器端收到消息，{}", msg.text());

        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:ss:mm"));
        // 回复浏览器
        ctx.writeAndFlush(new TextWebSocketFrame("服务器时间:" + dateTime + msg.text()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // id 表示channel唯一的值(longText)
        log.info("handlerAdded 被调用, {}", ctx.channel().id().asLongText());
        // id (shortText)不一定唯一
        log.info("handlerAdded 被调用, {}", ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("hanlderRemoved 被调用, {}", ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常发生", cause);
        ctx.close();
    }
}
