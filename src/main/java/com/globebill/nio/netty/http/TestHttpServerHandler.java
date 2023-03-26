package com.globebill.nio.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter 的子类
 * HttpObject 表示客户端和服务器端相互通讯的数据被封装成 HttpObject 类型
 *
 * @author Kavin
 * @date 2023/3/18 22:21
 */
@Slf4j
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 读取客户端数据
     *
     * @param ctx - 此 TestHttpServerHandler 所属的 ChannelHandlerContext
     * @param msg – 要处理的消息
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断 msg 是不是 HttpObject
        if (msg instanceof HttpRequest) {
            log.info("pipeline hashcode=" + ctx.pipeline().hashCode() + ", TestHttpserverHandler hash=" + this.hashCode());


            log.info("msg 类型 = " + msg.getClass());
            log.info("客户端地址 = " + ctx.channel().remoteAddress().toString().substring(1));
            log.info("客户端请求URL " + ((HttpRequest) msg).uri());

            // 回复信息给浏览器 [http协议]
            ByteBuf content = Unpooled.copiedBuffer("Hello, 我是服务器", CharsetUtil.UTF_8);

            // 构造一个http的响应，即HttpResponse
            DefaultHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            // 将构建好的 response 返回
            ctx.writeAndFlush(response);

            // http://127.0.0.1:8088/favicon.ico

        }
    }

}
