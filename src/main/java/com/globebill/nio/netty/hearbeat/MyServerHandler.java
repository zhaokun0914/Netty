package com.globebill.nio.netty.hearbeat;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyServerHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            String eventType;
            if (e.state() == IdleState.READER_IDLE) {
                eventType = "读空闲";
            } else if (e.state() == IdleState.WRITER_IDLE) {
                eventType = "写空闲";
            } else {
                eventType = "读写空闲";
            }
            log.info("{}--超时事件--{}", ctx.channel().remoteAddress().toString().substring(1), eventType);
        }
    }
}